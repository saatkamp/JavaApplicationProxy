package org.opentosca.driver.noop;

import org.opentosca.driver.Connection;
import org.opentosca.driver.Driver;
import org.opentosca.driver.DriverManagerConfig;

import static org.opentosca.driver.DriverManagerConfig.AbstractTopic;

public class NoopDriver implements Driver
{
  @Override
  public Connection connect(final AbstractTopic topic)
  {
    return new NoopConnection();
  }
}
