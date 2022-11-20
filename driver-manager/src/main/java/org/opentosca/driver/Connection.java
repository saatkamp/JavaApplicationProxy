package org.opentosca.driver;

public interface Connection
{
  void publish(final String topic, final String message);

  void subscribe(final String topic, final DriverListener listener);

  void close();
}
