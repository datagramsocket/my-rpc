package org.example;



public class Consumer {

    private static CommonInterface commonInterface = ProxyUtils.getProxy(CommonInterface.class);

    public static void main(String[] args) {
        for(int i=0; i<20; i++){
            new Thread(()-> {
                String ret = commonInterface.test("fcq");
                System.out.println(ret);
            }).start();
        }

    }

}
