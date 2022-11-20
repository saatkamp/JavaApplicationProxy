
## Java Prototype

| Module           | Description |
| ---------------- | ----------- |
| `driver-manager` | OpenTOSCA Driver Manager |
| `driver-amqp`    | OpenTOSCA AMQP Driver |
| `driver-mqtt`    | OpenTOSCA MQTT Driver |
| `subscriber`     | Subscriber Application |

The Java prototype uses Maven as its build system.
Therefore, just run `mvn clean package` in the `java` root directory in order to build the complete project.

### Subscriber Application

The subscriber application is a simple Spring Boot application having the `driver-manager` as a dependency.
By using Spring Boot, we going to compile a uber/fat JAR containing all dependant libraries.
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
