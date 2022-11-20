package org.opentosca.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class DriverManagerFactory
{
  private static Logger logger = LoggerFactory.getLogger(DriverManagerFactory.class);

  public static DriverManager getDriverManager(final String path)
  {
    return new DriverManager(getDriverManagerConfig(path));
  }

  public static DriverManagerConfig getDriverManagerConfig(final String path)
  {
    // Try to read file with a basic file input stream
    logger.info("Reading config file at \"{}\"...", path);
    try (InputStream is = new FileInputStream(new File(path)))
    {
      logger.info("Valid file on filesystem, read with FileInputStream");
      return readInputStream(is);
    }
    catch (IOException e)
    {
      // Fallback, read file from classpath
      logger.info("Looking up file from classpath");
      final ClassPathResource resource = new ClassPathResource(path);
      if (resource.exists() && resource.isReadable())
      {
        try (InputStream is = resource.getInputStream())
        {
          return readInputStream(is);
        }
        catch (IOException e1)
        {
          throw new RuntimeException("Error reading config file", e1);
        }
      }
      throw new RuntimeException("Error reading config file", e);
    }
  }

  private static DriverManagerConfig readInputStream(InputStream is)
  {
    final Yaml yaml = new Yaml();
    return yaml.loadAs(is, DriverManagerConfig.class);
  }
}
