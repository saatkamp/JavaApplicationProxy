package org.opentosca.driver;

public class Message<T> {

    protected T payload;

    public Message() {
    }

    public Message(T payload) {
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
