package org.example;

import java.io.ObjectInputStream;

public class Package {

    private Header header;
    private Object body;

    public Package(Header header, Object body) {
        this.header = header;
        this.body = body;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Package{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
