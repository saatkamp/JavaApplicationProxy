package org.opentosca.driver.noop;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.opentosca.driver.Connection;
import org.opentosca.driver.DriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class NoopConnection implements Connection
{
  private static Logger logger = LoggerFactory.getLogger(NoopConnection.class);

  private SetMultimap<String, DriverListener> listenerMap = HashMultimap.create();

  @Override
  public void publish(final String topic, final String message)
  {
    logger.warn("[NOOP] Publishing to topic \"{}\": {}", topic, message);
    try
    {
      TimeUnit.SECONDS.sleep(1);
      listenerMap.get(topic).forEach(listener -> listener.onMessage(message));
    }
    catch (InterruptedException e)
    {
      // Ignore
    }
  }

  @Override
  public void subscribe(final String topic, final DriverListener listener)
  {
    logger.warn("[NOOP] Subscribing to topic \"{}\"", topic);
    listenerMap.put(topic, listener);
  }

  @Override
  public void close()
  {

  }
}
