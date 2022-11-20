package org.opentosca.driver;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.opentosca.driver.noop.NoopDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class DriverUtils
{
  private static Logger logger = LoggerFactory.getLogger(DriverUtils.class);

  private static Cache<String, Driver> cache = CacheBuilder.newBuilder().build();

  @SuppressWarnings("unchecked")
  static Driver forName(final String name)
  {
    // Load from cache
    logger.info("Loading driver \"{}\"...", name);
    Driver driver = cache.getIfPresent(name);
    if (driver != null)
    {
      logger.info("Found driver in cache");
      return driver;
    }
    // Instantiate a new one
    try
    {
      logger.info("Create new instance of driver \"{}\"", name);
      Class<Driver> clazz;
      try
      {
        clazz = (Class<Driver>) ClassUtils.getClass(name);
      }
      catch (ClassNotFoundException e)
      {
        logger.error("Driver \"{}\" not found, falling back to NoopDriver", name);
        clazz = (Class<Driver>) ClassUtils.getClass(NoopDriver.class.getCanonicalName());
      }
      driver = ConstructorUtils.invokeConstructor(clazz);
      cache.put(name, driver);
      return driver;
    }
    catch (Exception e)
    {
      throw new RuntimeException("Class \"" + name + "\" could not be instantiated", e);
    }
  }
}
