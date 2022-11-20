package org.opentosca.driver.noop;

import org.opentosca.driver.Connection;
import org.opentosca.driver.Driver;

import static org.opentosca.driver.DriverManagerConfig.Topic;

public class NoopDriver implements Driver
{
  @Override
  public Connection connect(final Topic topic)
  {
    return new NoopConnection();
  }
}
