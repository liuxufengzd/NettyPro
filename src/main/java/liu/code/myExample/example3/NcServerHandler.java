package liu.code.myExample.example3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.UUID;

public class NcServerHandler extends ChannelInboundHandlerAdapter {
    private int count;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf){
            ByteBuf byteBuf = (ByteBuf)msg;
            System.out.println("received: "+byteBuf.toString(CharsetUtil.UTF_8));
        }
        System.out.println("服务器接收到消息量=" + (++this.count));
        ctx.writeAndFlush(Unpooled.copiedBuffer(UUID.randomUUID().toString().getBytes()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
