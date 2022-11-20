package org.opentosca.driver.amqp;

import com.google.common.collect.Sets;
import com.rabbitmq.client.*;
import org.opentosca.driver.Connection;
import org.opentosca.driver.DriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

public class AmqpConnection implements Connection
{
  private static Logger logger = LoggerFactory.getLogger(AmqpConnection.class);

  private final com.rabbitmq.client.Connection connection;
  private final Set<Channel> channels = Sets.newHashSet();

  AmqpConnection(final com.rabbitmq.client.Connection connection)
  {
    this.connection = connection;
  }

  @Override
  public void publish(final String topic, final String message)
  {
    logger.info("Publishing to topic \"{}\": {}", topic, message);
    try
    {
      final Channel channel = connection.createChannel();
      channel.exchangeDeclare(topic, BuiltinExchangeType.TOPIC);
      channel.basicPublish(topic, "", null, message.getBytes());
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void subscribe(final String topic, final DriverListener listener)
  {
    logger.info("Subscribing to topic \"{}\"...", topic);
    try
    {
      final Channel channel = connection.createChannel();
      channel.exchangeDeclare(topic, BuiltinExchangeType.TOPIC);
      final String queueName = channel.queueDeclare().getQueue();
      channel.queueBind(queueName, topic, "");
      final Consumer consumer = new DefaultConsumer(channel)
      {
        @Override
        public void handleDelivery(final String consumerTag, final Envelope envelope,
                                   final AMQP.BasicProperties properties, final byte[] body) throws IOException
        {
          final String message = new String(body, "UTF-8");
          logger.info("Received AMQP message on topic \"{}\": {}", topic, message);
          listener.onMessage(message);
        }
      };
      channel.basicConsume(queueName, true, consumer);
      channels.add(channel);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close()
  {
    try
    {
      for (Channel channel : channels)
      {
        channel.close();
      }
      connection.close();
      logger.info("Connection closed");
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }
}
