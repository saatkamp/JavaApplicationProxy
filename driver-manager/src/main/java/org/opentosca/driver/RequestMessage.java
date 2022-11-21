package org.opentosca.driver;

import com.google.common.base.MoreObjects;

public class RequestMessage<T> extends Message<T> {

    private String reply_to;

    public String getReply_to() {
        return reply_to;
    }

    public void setReply_to(String reply_to) {
        this.reply_to = reply_to;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("replyTo", reply_to)
                .add("payload", payload)
                .toString();
    }
}
