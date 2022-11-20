package org.opentosca.driver.mqtt;

import org.opentosca.driver.Connection;
import org.opentosca.driver.Driver;

import static org.opentosca.driver.DriverManagerConfig.Topic;

public class MqttDriver implements Driver
{
  @Override
  public Connection connect(Topic topic)
  {
    return new MqttConnection(topic.getConnection());
  }
}
