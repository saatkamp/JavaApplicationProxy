package org.opentosca.driver;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opentosca.driver.DriverManagerConfig.RequestReplyTopic;
import static org.opentosca.driver.DriverManagerConfig.Topic;

public final class DriverManager {
    private static Logger logger = LoggerFactory.getLogger(DriverManager.class);

    private final DriverManagerConfig config;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Cache<Topic, Connection> connectionCache = CacheBuilder.newBuilder().build();

    private final Cache<RequestReplyTopic, Connection> reqResConnectionCache = CacheBuilder.newBuilder().build();
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    DriverManager(final DriverManagerConfig config) {
        Preconditions.checkNotNull(config);
        this.config = config;
    }

    public <T> void publish(final String sensorName, final T payload) {
        // (1) Determine topic definitions based on given sensor name
        // (2) Send payload to all topic definitions
        // (3) Therefore, create a connection with the determined driver
        // (4) Publish payload through the created connection (threaded)
        final List<Callable<Boolean>> tasks = Lists.newArrayList();
        if (config.getSensors().get(sensorName) == null)
            throw new RuntimeException("A valid sensor name must be given");
        final List<Topic> topics = config.getTopics(sensorName);
        logger.info("Found <{}> topic(s) to publish data", topics.size());
        for (final Topic topic : topics) {
            Connection conn = connectionCache.getIfPresent(topic);
            if (conn == null) {
                conn = DriverUtils
                        .forName(topic.getDriver())
                        .connect(topic);
                connectionCache.put(topic, conn);
            }
            logger.info("Publish payload: {}", payload);
            tasks.add(callable(conn, topic.getName(), new SensorMessage<>(config.getSensors().get(sensorName), payload)));
        }
        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        logger.info("Published data to <{}> topic(s)", topics.size());
    }

    private Callable<Boolean> callable(final Connection connection, final String topicName, final Message message) {
        return () ->
        {
            try {
                connection.publish(topicName, objectMapper.writeValueAsString(message));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return true;
        };
    }

    public <T> void subscribe(final String sensorName, MessageListener<T> messageListener, Class<T> payloadType) {
        final JavaType javaType = objectMapper.getTypeFactory().constructFromCanonical(payloadType.getCanonicalName());
        this.subscribe(sensorName, messageListener, javaType);
    }

    public <T> void subscribe(final String sensorName, final MessageListener<T> messageListener, final JavaType payloadType) {
        // (1) Determine topic definitions based on given type
        // (2) Subscribe to all topic definitions
        // (3) Therefore, get a connection with the determined driver
        final List<Topic> topics = config.getTopics(sensorName);
        logger.info("Found <{}> topic(s) to subscribe", topics.size());
        for (final Topic topic : topics) {
            Connection conn = connectionCache.getIfPresent(topic);
            if (conn == null) {
                conn = DriverUtils
                        .forName(topic.getDriver())
                        .connect(topic);
                connectionCache.put(topic, conn);
            }
            conn.subscribe(topic.getName(), m ->
            {
                try {
                    final JavaType messageType = objectMapper.getTypeFactory()
                            .constructParametricType(Message.class, payloadType);
                    messageListener.onMessage(objectMapper.readValue(m, messageType));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        logger.info("Subscribed to <{}> topic(s)", topics.size());
    }

    public <T> void subscribeReqRes(MessageListener<T> messageListener, Class<T> payloadType) {
        final JavaType javaType = objectMapper.getTypeFactory().constructFromCanonical(payloadType.getCanonicalName());
        // (2) Subscribe to topic
        // (3) Therefore, get a connection with the determined driver
        final RequestReplyTopic requestResponseTopic = config.getRequestReplyTopic();
        Connection conn = reqResConnectionCache.getIfPresent(requestResponseTopic);
        if (conn == null) {
            conn = DriverUtils
                    .forName(requestResponseTopic.getDriver())
                    .connect(requestResponseTopic);
            reqResConnectionCache.put(requestResponseTopic, conn);
        }
        conn.subscribe(requestResponseTopic.getName(), m ->
        {
            logger.info("Parsing message...");
            try {
                final JavaType messageType = objectMapper.getTypeFactory()
                        .constructParametricType(RequestMessage.class, javaType);
                messageListener.onMessage(objectMapper.readValue(m, messageType));
            } catch (IOException e) {
                logger.error("Error while parsing message...", e);
                throw new RuntimeException(e);
            }
        });
        logger.info("Subscribed to \"{}\"", requestResponseTopic.getName());
    }

    public void close() {
        executor.shutdownNow();
        connectionCache.asMap().values().forEach(Connection::close);
    }
}
