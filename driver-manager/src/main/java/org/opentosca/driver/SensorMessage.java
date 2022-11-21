package org.opentosca.driver;

import java.util.Map;

import com.google.common.base.MoreObjects;

public class SensorMessage<T> extends Message<T> {

    private Map<String, Object> sensor;

    public Map<String, Object> getSensor()
    {
        return sensor;
    }

    public SensorMessage() {
    }

    public SensorMessage(Map<String, Object> sensor, T payload) {
        super(payload);
        this.sensor = sensor;
    }

    public void setSensor(Map<String, Object> sensor)
    {
        this.sensor = sensor;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("sensor", sensor)
                .add("payload", payload)
                .toString();
    }
}
