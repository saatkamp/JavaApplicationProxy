package org.opentosca.driver.app;

import org.opentosca.driver.DriverManager;
import org.opentosca.driver.DriverManagerFactory;
import org.opentosca.driver.Message;
import org.opentosca.driver.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner, MessageListener<RequestMessage>
{
  private static Logger logger = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args)
  {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(String... args) throws Exception
  {
    final DriverManager manager = DriverManagerFactory.getDriverManager(args[0]);
    manager.subscribeReqRes(this, RequestMessage.class);
    Thread.currentThread().join();
    manager.close();
  }

  @Override
  public void onMessage(final Message<RequestMessage> message)
  {
    RequestMessage requestMessage = message.getPayload();


    logger.info("Received payload: {}", message.getPayload());


  }
}
