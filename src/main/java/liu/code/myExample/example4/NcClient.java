package liu.code.myExample.example4;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class NcClient {
    public static void main(String[] args) {
        new NcClient().run(new InetSocketAddress("127.0.0.1",8001));
    }

    public void run(SocketAddress socketAddress){
        NioEventLoopGroup clientGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new MessageDecoder(),new MessageEncoder(),new NcClientHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.connect(socketAddress).sync();
            System.out.println("connected...");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clientGroup.shutdownGracefully();
        }
    }
}
