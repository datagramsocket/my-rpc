package org.example;

public class CommonInterfaceImpl implements CommonInterface{
    @Override
    public String test(String msg) {
        return msg + " provider";
    }
}
