package org.opentosca.driver;

import com.google.common.base.MoreObjects;

import java.util.Map;

public class Message<T>
{
  private Map<String, Object> sensor;

  private T payload;

  public Message()
  {
  }

  Message(final Map<String, Object> sensor, final T payload)
  {
    this.sensor = sensor;
    this.payload = payload;
  }

  public Map<String, Object> getSensor()
  {
    return sensor;
  }

  public void setSensor(Map<String, Object> sensor)
  {
    this.sensor = sensor;
  }

  public T getPayload()
  {
    return payload;
  }

  public void setPayload(T payload)
  {
    this.payload = payload;
  }

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
      .add("sensor", sensor)
      .add("payload", payload)
      .toString();
  }
}
