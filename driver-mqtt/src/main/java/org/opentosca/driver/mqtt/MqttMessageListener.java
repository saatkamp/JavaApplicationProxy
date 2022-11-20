package org.opentosca.driver.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.opentosca.driver.DriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttMessageListener implements IMqttMessageListener
{
  private static Logger logger = LoggerFactory.getLogger(MqttMessageListener.class);

  private final DriverListener messageListener;

  MqttMessageListener(final DriverListener messageListener)
  {
    this.messageListener = messageListener;
  }

  @Override
  public void messageArrived(final String topic, final MqttMessage message) throws Exception
  {
    logger.info("Received MQTT message on topic \"{}\": {}", topic, message.toString());
    this.messageListener.onMessage(new String(message.getPayload(), "UTF-8"));
  }
}
