package org.opentosca.driver.mqtt;

import com.google.common.collect.Sets;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.opentosca.driver.Connection;
import org.opentosca.driver.DriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

public class MqttConnection implements Connection {
    private static Logger logger = LoggerFactory.getLogger(MqttConnection.class);

    private final MqttClient client;
    private final Set<String> subscriptions = Sets.newHashSet();

    MqttConnection(final String broker) {
        final MemoryPersistence persistence = new MemoryPersistence();
        final MqttConnectOptions options = new MqttConnectOptions();

        try {
            final String clientId = UUID.randomUUID().toString();
            this.client = new MqttClient("tcp://" + broker, clientId, persistence);
            this.client.connect(options);
            logger.info("Connected to MQTT broker at \"{}\" ({})", broker, clientId);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void publish(String topic, String message) {
        logger.info("Publishing to topic \"{}\": {}", topic, message);
        try {
            client.publish(topic, new MqttMessage(message.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(String topic, DriverListener listener) {
        logger.info("Subscribing to topic \"{}\"...", topic);
        try {
            client.subscribe(topic, new MqttMessageListener(listener));
            subscriptions.add(topic);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            for (String subscription : subscriptions) {
                client.unsubscribe(subscription);
            }
            client.disconnect();
            logger.info("Connection closed");
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
