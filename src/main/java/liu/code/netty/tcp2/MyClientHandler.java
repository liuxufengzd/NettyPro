package liu.code.netty.tcp2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

public class MyClientHandler extends SimpleChannelInboundHandler<MessagePackage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePackage msg) throws Exception {
        String message = new String(msg.getContent(), StandardCharsets.UTF_8);
        System.out.println("received from server:"+message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String message = "I love you!";
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < 5; i++) {
            MessagePackage messagePackage = new MessagePackage(bytes.length, bytes);
            ctx.writeAndFlush(messagePackage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
