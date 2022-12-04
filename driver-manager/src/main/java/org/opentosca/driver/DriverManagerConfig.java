package org.opentosca.driver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Objects;

public final class DriverManagerConfig {
    private Map<String, Map<String, Object>> sensors;

    private List<Topic> topics;

    private RequestReplyTopic requestReplyTopic;

    public Map<String, Map<String, Object>> getSensors() {
        return sensors;
    }

    public void setSensors(Map<String, Map<String, Object>> sensors) {
        this.sensors = sensors;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public List<Topic> getTopics(final String sensor) {
        return topics.stream()
                .filter(t -> t.getSensor().equalsIgnoreCase(sensor))
                .collect(Collectors.toList());
    }

    public RequestReplyTopic getRequestReplyTopic() {
        return requestReplyTopic;
    }

    public void setRequestReplyTopic(final RequestReplyTopic requestReplyTopic) {
        this.requestReplyTopic = requestReplyTopic;
    }

    public void setTopics(final List<Topic> topics) {
        this.topics = topics;
    }

    public static class AbstractTopic {
        protected String name;

        protected String driver;

        protected String connection;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(final String driver) {
            this.driver = driver;
        }

        public String getConnection() {
            return connection;
        }

        public void setConnection(final String connection) {
            this.connection = connection;
        }
    }

    public static class Topic extends AbstractTopic {

        private String sensor;

        public String getSensor() {
            return sensor;
        }

        public void setSensor(String sensor) {
            this.sensor = sensor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Topic topic = (Topic) o;
            return Objects.equal(name, topic.name) &&
                    Objects.equal(sensor, topic.sensor) &&
                    Objects.equal(driver, topic.driver) &&
                    Objects.equal(connection, topic.connection);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, sensor, driver, connection);
        }
    }

    public static class RequestReplyTopic extends AbstractTopic {

        public ProxyLocation proxyFor;

        public static class ProxyLocation {
            public String protocol;
            public String location;
            public String port;
        }
    }
}
