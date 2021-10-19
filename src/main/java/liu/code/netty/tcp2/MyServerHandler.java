package liu.code.netty.tcp2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MyServerHandler extends SimpleChannelInboundHandler<MessagePackage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePackage msg) throws Exception {
        String message = new String(msg.getContent(), StandardCharsets.UTF_8);
        System.out.println("from client:"+message);
        byte[] returnMsg = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
        ctx.writeAndFlush(new MessagePackage(returnMsg.length,returnMsg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
