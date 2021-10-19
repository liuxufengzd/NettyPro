package liu.code.myExample.example1;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupServerHandler extends SimpleChannelInboundHandler<String> {
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//        System.out.println("GroupServerHandler's thread:"+Thread.currentThread());
//        ctx.executor().execute(()-> System.out.println("GroupServerHandler's added task 1 thread:"+Thread.currentThread()));
//        ctx.channel().eventLoop().execute(()-> System.out.println("GroupServerHandler's added task 2 thread:"+Thread.currentThread()));
        Channel thisChannel = ctx.channel();
        channelGroup.forEach(channel -> {
            if (thisChannel != channel)
                channel.writeAndFlush("Client->" + thisChannel.remoteAddress() + " send:" + msg + "\r\n");
        });
        ctx.fireChannelRead(msg.toUpperCase());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+" is active!");
        channelGroup.add(ctx.channel());
        channelGroup.writeAndFlush("[client:]"+ctx.channel().remoteAddress()+" attends the chat "+dateFormat.format(new Date())+"\r\n");
    }
    // client调用disconnect(),shutdownGracefully()等正常关闭
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelGroup.remove(ctx.channel());
        channelGroup.writeAndFlush("[client:]"+ctx.channel().remoteAddress()+" leaves the chat "+dateFormat.format(new Date())+"\r\n");
        System.out.println("clients number:"+channelGroup.size());
        System.out.println(ctx.channel().remoteAddress()+" is inactive!");
    }
    // channel被客户端强制非正常关闭等
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error exit!");
        ctx.close();
    }
}
