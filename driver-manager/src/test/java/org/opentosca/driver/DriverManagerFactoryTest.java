package org.opentosca.driver;

import org.junit.Assert;
import org.junit.Test;

public class DriverManagerFactoryTest
{
  @Test
  public void createWithFileFromClasspath() throws Exception
  {
    DriverManagerFactory.getDriverManager("driver-manager.yml");
    DriverManagerConfig config = DriverManagerFactory.getDriverManagerConfig("driver-manager.yml");
    Assert.assertEquals(2, config.getTopics().size());
    Assert.assertEquals(1, config.getTopics("temp-frontdoor").size());
    Assert.assertEquals(1, config.getTopics("temp-livingroom").size());
    Assert.assertEquals(2, config.getSensors().size());
    Assert.assertNull(config.getSensors().get("test"));
    Assert.assertNotNull(config.getSensors().get("temp-livingroom"));
  }
}
