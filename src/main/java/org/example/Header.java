package org.example;

import java.io.Serializable;

public class Header implements Serializable {

    private int flag;
    private int dataLength;
    private long requestId;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "Header{" +
                "flag=" + flag +
                ", dataLength=" + dataLength +
                ", requestId=" + requestId +
                '}';
    }
}
