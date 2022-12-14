package org.opentosca.driver.amqp;

import com.rabbitmq.client.ConnectionFactory;
import org.opentosca.driver.Connection;
import org.opentosca.driver.Driver;
import org.opentosca.driver.DriverManagerConfig;

import static org.opentosca.driver.DriverManagerConfig.AbstractTopic;

public class AmqpDriver implements Driver
{
  @Override
  public Connection connect(final AbstractTopic topic)
  {
    final String[] params = topic.getConnection().split(":");
    final ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(params[0]);
    factory.setPort(Integer.valueOf(params[1]));
    try
    {
      return new AmqpConnection(factory.newConnection());
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }
}
