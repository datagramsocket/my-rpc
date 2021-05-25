package org.example;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;


public class ProviderChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Package message = (Package)msg;
        Header header = message.getHeader();
        Body body = (Body)message.getBody();


        Object retObj = body.getArgs()[0] + " rpc test";
        byte[] retObjBytes = RpcUtils.serializeObject(retObj);



        Header retHeader = new Header();
        retHeader.setDataLength(retObjBytes.length);
        retHeader.setRequestId(header.getRequestId());
        byte[] retHeaderBytes =  RpcUtils.serializeObject(retHeader);


        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(retHeaderBytes.length + retObjBytes.length);
        byteBuf.writeBytes(retHeaderBytes);
        byteBuf.writeBytes(retObjBytes);
        ChannelFuture channelFuture = ctx.writeAndFlush(byteBuf);
        channelFuture.sync();

    }
}
