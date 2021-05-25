package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class ProxyUtils {

    public static  <T> T getProxy(Class<T> clazz){
        Class[] classes = new Class[]{clazz};
        Object o = Proxy.newProxyInstance(ProxyUtils.class.getClassLoader(), classes, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //1.拿到连接  2.序列化对象构建信息  3.发送到服务端
                InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 9090);
                Channel channel = ConnectionPool.getConnection(inetSocketAddress);
                Header header = new Header();
                header.setFlag(0);
                long requestId = UUID.randomUUID().getLeastSignificantBits();
                header.setRequestId(requestId);
                Body body = new Body();
                body.setArgs(args);
                body.setClazzName(clazz.getName());
                body.setMethodName(method.getName());

                ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream1);
                objectOutputStream.writeObject(body);
                byte[] bodyBytes = byteArrayOutputStream1.toByteArray();

                header.setDataLength(bodyBytes.length);
                ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(byteArrayOutputStream2);
                objectOutputStream2.writeObject(header);
                byte[] headerBytes =  byteArrayOutputStream2.toByteArray();


                ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(headerBytes.length + bodyBytes.length);
                byteBuf.writeBytes(headerBytes);
                byteBuf.writeBytes(bodyBytes);
                CompletableFuture<Object> completableFuture = new CompletableFuture<>();
                ConsumerChannelHandler.resultMap.put(requestId, completableFuture);

                ChannelFuture channelFuture = channel.writeAndFlush(byteBuf);
                channelFuture.sync();

                Object retObject = completableFuture.get();
                ConsumerChannelHandler.resultMap.remove(requestId);

                return retObject;
            }
        });
        return (T)o;
    }

}
