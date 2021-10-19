package liu.code.myExample.example7;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.net.URISyntaxException;

public class TestClient {
    public static void main(String[] args) throws URISyntaxException {
        new TestClient().run("127.0.0.1", 8080);
    }

    public void run(String host, int port) throws URISyntaxException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpClientCodec(),
                                new HttpObjectAggregator(512 * 1024),
                                new HttpClientHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();

            URI uri = new URI("/outbound/api/v1/test");
            String response = "This is client!";
            ByteBuf byteBuf = Unpooled.copiedBuffer(response.getBytes(CharsetUtil.UTF_8));
            DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString(),byteBuf);
            httpRequest.headers().set(HttpHeaderNames.HOST, host);
            httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes());
            channelFuture.channel().writeAndFlush(httpRequest);

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
