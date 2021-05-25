package org.example;



import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class Provider {

    public static void main(String[] args) {
        startServer();
    }

    public static void startServer(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<NioSocketChannel>() {

            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                ChannelPipeline pipeline = nioSocketChannel.pipeline();
                pipeline.addLast(new ByteToMessageDecoder() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                        while(true){
                            if(in.readableBytes() >= 87){
                                byte[] headerBytes = new byte[87];
                                in.getBytes(in.readerIndex(), headerBytes);
                                Header header = (Header)RpcUtils.deserializeBytes(headerBytes);
                                int dataLength = header.getDataLength();
                                if(in.readableBytes() >= (dataLength + 87)){
                                    byte[] bodyBytes = new byte[dataLength];
                                    in.readBytes(87);
                                    in.readBytes(bodyBytes);
                                    Object body = RpcUtils.deserializeBytes(bodyBytes);
                                    out.add(new Package(header, body));
                                }else {
                                    break;
                                }
                            }else {
                                break;
                            }
                        }

                    }
                });
                pipeline.addLast(new ProviderChannelHandler());

            }

        });
        serverBootstrap.bind(9090);
        System.out.println("server bind at 9090");
    }

}
