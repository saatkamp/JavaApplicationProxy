package org.opentosca.driver;

public interface MessageListener<T>
{
  void onMessage(final Message<T> message);
}
