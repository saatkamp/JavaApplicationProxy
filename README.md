# Java Application Proxy [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This prototype demonstrates how HTTP requests can be wrapped into a message that is sent to a topic or
queue using different messaging technologies.
Therefore, it uses a generic driver approach, whereby drivers can be exchanged for each startup.
A driver implements the communication to a message broker such as [Mosquitto](https://mosquitto.org/)
or [RabbitMQ](https://rabbitmq.com/).
The corresponding Publisher application is available as the [Python Application Proxy](https://github.com/saatkamp/PythonApplicationProxy).

Currently, only a MQTT driver is implemented.

## Set Up

1. Run `mvn packge`
2. Configure your MQTT broker in the `driver-manager.yml`. For example:
   ```yaml
   requestReplyTopic:
     name: job
     driver: org.opentosca.driver.mqtt.MqttDriver
     connection: 1.2.3.4:1883
     proxyFor:
       protocol: http
       location: 5.6.7.8
       port: 8080
   ```
3. Start the application by passing corresponding driver to the JVM:
   ```shell
    java -Dloader.path=./driver-mqtt/target/driver-mqtt-1.0.jar -jar ./subscriber/target/subscriber-1.0.jar driver-manager.yml
   ```
   You must pass the path to the config file as the first argument of the application.

## Java Prototype

| Module           | Description                                     |
|------------------|-------------------------------------------------|
| `driver-manager` | OpenTOSCA Driver Manager                        |
| `driver-amqp`    | OpenTOSCA AMQP Driver (currently not supported) |
| `driver-mqtt`    | OpenTOSCA MQTT Driver                           |
| `subscriber`     | Subscriber Application                          |

### Subscriber Application

The subscriber application is a simple Spring Boot application having the `driver-manager` as a dependency.
By using Spring Boot, we are going to compile an uber/fat JAR containing all dependant libraries.
Basically, the subscriber application can simply be run with `java -jar subscriber-1.0.jar "driver-manager.yml"`.
Anyhow, since the drivers are packaged and deployed in separate JAR files, the subscriber application has to be aware of the classpath where the drivers are located.
Therefore, the application has to be started by specifying the `loader.path` property as an additional classpath location:
```
java -cp subscriber-1.0.jar -Dloader.path="./driver" org.springframework.boot.loader.PropertiesLauncher "driver-manager.yml"

java -Dloader.path=.\driver-mqtt\target\driver-mqtt-1.0.jar -jar .\subscriber\target\subscriber-1.0.jar driver-manager.yml
```

### OpenTOSCA AMQP/MQTT Driver

The respective drivers are built using Maven's Shade Plugin in order to build a uber JAR containing all dependant libraries.
During installation, we just have to put the driver JAR file to a certain location on the target host, which is in turn known as the `loader.path` for the subscriber application.
