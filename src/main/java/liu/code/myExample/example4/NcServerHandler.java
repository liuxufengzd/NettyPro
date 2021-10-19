package liu.code.myExample.example4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.UUID;

public class NcServerHandler extends SimpleChannelInboundHandler<MessagePackage> {
    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePackage msg) throws Exception {
        int length = msg.getLength();
        String content = new String(msg.getContent(), CharsetUtil.UTF_8);
        System.out.println("received->" + length + ":" + content + "--" + count++);
        String response = UUID.randomUUID() + "I have received!";
        ctx.writeAndFlush(new MessagePackage(response.length(), response.getBytes(CharsetUtil.UTF_8)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
