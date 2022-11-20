package org.opentosca.driver;

import static org.opentosca.driver.DriverManagerConfig.Topic;

public interface Driver
{
  Connection connect(final Topic topic);
}
