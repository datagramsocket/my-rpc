package org.example;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerChannelHandler extends ChannelInboundHandlerAdapter {

    public static Map<Long, CompletableFuture> resultMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Package message = (Package) msg;
        Header header = message.getHeader();
        Object retObject = message.getBody();
        long requestId = header.getRequestId();
        resultMap.get(requestId).complete(retObject);

    }
}
