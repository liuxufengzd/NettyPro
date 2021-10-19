package liu.code.myExample.example4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.UUID;

public class NcClientHandler extends SimpleChannelInboundHandler<MessagePackage> {
    private int count;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i=0;i<10;i++){
            String data = UUID.randomUUID().toString() + ":" + i;
            byte[] dataBytes = data.getBytes(CharsetUtil.UTF_8);
            ctx.writeAndFlush(new MessagePackage(dataBytes.length, dataBytes));
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePackage msg) throws Exception {
        String data = new String(msg.getContent(), CharsetUtil.UTF_8);
        System.out.println("received: " + data + "->" + count++);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
