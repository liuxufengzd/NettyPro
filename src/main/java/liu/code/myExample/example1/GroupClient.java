package liu.code.myExample.example1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GroupClient {
    private static final EventExecutorGroup executorGroup = new DefaultEventExecutorGroup(10);
    public static long count = 1000;

    public static void main(String[] args) {
        new GroupClient().run(new InetSocketAddress("127.0.0.1", 8080));
    }

    public void run(SocketAddress socketAddress) {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder(), new StringDecoder(), new GroupClientHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.connect(socketAddress).sync();
            Channel channel = channelFuture.channel();
//            Scanner scanner = new Scanner(System.in);
//            while (scanner.hasNextLine()) {
//                String data = scanner.nextLine();
//                if ("exit".equals(data))
//                    channel.disconnect().sync();
//                if ("start".equals(data))
//                    bootstrap.connect(socketAddress).sync();
//                if ("end".equals(data))
//                    break;
//                channel.writeAndFlush(data + "\r\n");
//            }
            long initCount = count;
            executorGroup.scheduleWithFixedDelay(() -> {
                if (count > 0) {
                    channel.writeAndFlush("data:" + (initCount - count));
                    count--;
                }
            }, 0, 50, TimeUnit.MILLISECONDS);
            channel.closeFuture().sync();
            System.out.println("closed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
