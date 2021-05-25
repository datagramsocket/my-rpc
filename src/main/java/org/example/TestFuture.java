package org.example;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class TestFuture {

    public static void main(String[] args) throws IOException {
        CompletableFuture<String> completableFutureFuture = new CompletableFuture();
        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getId() + " start get");
                String s = completableFutureFuture.get();
                System.out.println(Thread.currentThread().getId() + " finish get" + s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getId() + "  sleep 3 second, and complete");
                completableFutureFuture.complete("finish");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        //System.in.read();
    }
}
