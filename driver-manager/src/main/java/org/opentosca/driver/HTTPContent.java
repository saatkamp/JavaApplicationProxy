package org.opentosca.driver;


public class HTTPContent {

    private String method;
    private String path;
    private String headers;
    private String payload;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "HTTPContent{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", headers='" + headers + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
