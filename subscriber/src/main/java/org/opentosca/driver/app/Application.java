package org.opentosca.driver.app;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.collect.Lists;
import com.sun.net.httpserver.Headers;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.opentosca.driver.DriverManager;
import org.opentosca.driver.DriverManagerFactory;
import org.opentosca.driver.HTTPContent;
import org.opentosca.driver.Message;
import org.opentosca.driver.MessageListener;
import org.opentosca.driver.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner, MessageListener<HTTPContent> {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private DriverManager manager;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        this.manager = DriverManagerFactory.getDriverManager(args[0]);
        manager.subscribeReqRes(this, HTTPContent.class);
        Thread.currentThread().join();
        manager.close();
    }

    @Override
    public void onMessage(Message<HTTPContent> message) {
        if (message instanceof RequestMessage) {
            RequestMessage<HTTPContent> req = (RequestMessage<HTTPContent>) message;
            logger.info("Received request: {}", req);
            HTTPContent httpContent = req.getPayload();

            List<Header> headers = parseHttpHeaders(httpContent.getHeaders());

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                RequestBuilder builder = RequestBuilder.get();
                switch (httpContent.getMethod().toLowerCase()) {
                    case "post":
                        builder = RequestBuilder.post();
                        BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
                        basicHttpEntity.setContent(
                                new ByteArrayInputStream(req.getPayload().getPayload().getBytes())
                        );
                        builder.setEntity(basicHttpEntity);

                        headers.removeIf(header -> header.getName().equalsIgnoreCase("Content-Length"));

                        break;
                    case "patch":
                        builder = RequestBuilder.patch();
                        break;
                    case "put":
                        builder = RequestBuilder.put();
                        break;
                    case "delete":
                        builder = RequestBuilder.delete();
                        break;
                    case "head":
                        builder = RequestBuilder.head();
                        break;
                    case "options":
                        builder = RequestBuilder.options();
                        break;
                    case "trace":
                        builder = RequestBuilder.trace();
                        break;
                }

                headers.forEach(builder::addHeader);

                builder.setUri("http://localhost:8080" + httpContent.getPath());

                logger.info("Sending request to: {}", builder.getUri());

                try (CloseableHttpResponse response = client.execute(builder.build())) {
                    HttpEntity entity = response.getEntity();

                    this.manager.publish(req.getReply_to(), new String(entity.getContent().readAllBytes(), StandardCharsets.UTF_8).replaceAll("\n", "").replaceAll("\\\"", "\""));
                }
            } catch (IOException e) {
                logger.error("Error while creating HTTP request..!", e);
                throw new RuntimeException(e);
            }
        }
    }

    private List<Header> parseHttpHeaders(String stringHeaders) {
        List<Header> headers = Lists.newArrayList();
        for (String header : stringHeaders.split("\n")) {
            if (header.length() > 0) {
                String[] split = header.split(": ");
                headers.add(new BasicHeader(split[0], split[1]));
            }
        }

        return headers;
    }
}
