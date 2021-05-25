package org.example;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接池
 * */
public class ConnectionPool {



    private static  int initSize = 1;

    private static  Channel[] channels = new Channel[initSize];

    private ConnectionPool(){


    }

    private static Map<SocketAddress, ConnectionPool> poolMap = new ConcurrentHashMap();


    public static synchronized Channel getConnection(InetSocketAddress inetSocketAddress) throws InterruptedException {
        ConnectionPool connectionPool = poolMap.get(inetSocketAddress);
        if(connectionPool == null){
            connectionPool = new ConnectionPool();
            poolMap.putIfAbsent(inetSocketAddress, connectionPool);
        }
        Random random = new Random();
        int randomInt = random.nextInt(initSize);
        Channel channel = channels[randomInt];
        if(channel == null){
            Bootstrap bootstrap = new Bootstrap();
            EventLoopGroup workerGroup = new NioEventLoopGroup(1);
            bootstrap.channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>(){

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
                    pipeline.addLast(new ConsumerChannelHandler());

                }
            });
            bootstrap.group(workerGroup);
            ChannelFuture sync = bootstrap.connect(inetSocketAddress).sync();
            channel = sync.channel();
            channels[randomInt] = channel;

        }
        return channel;
    }

    public static void main(String[] args) {
        Random random = new Random();

        System.out.println(random.nextInt(initSize));
    }

}
