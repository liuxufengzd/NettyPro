package liu.code.myExample.example3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NcClientHandler extends ChannelInboundHandlerAdapter {
    private int count;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i=0;i<10;i++){
            ctx.writeAndFlush(Unpooled.copiedBuffer(("Hello~"+i).getBytes(CharsetUtil.UTF_8)));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.getClass().getName());
        if (msg instanceof ByteBuf){
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println("from server:"+byteBuf.toString(CharsetUtil.UTF_8)+"->"+count++);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
