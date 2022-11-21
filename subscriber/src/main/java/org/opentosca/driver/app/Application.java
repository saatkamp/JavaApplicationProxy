package org.opentosca.driver.app;

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

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final DriverManager manager = DriverManagerFactory.getDriverManager(args[0]);
        manager.subscribeReqRes(this, HTTPContent.class);
        Thread.currentThread().join();
        manager.close();
    }

    @Override
    public void onMessage(Message<HTTPContent> message) {
        if (message instanceof RequestMessage) {
            RequestMessage<HTTPContent> req = (RequestMessage<HTTPContent>) message;

            logger.info("Received request: {}", req);
        }
    }
}
