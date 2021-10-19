package liu.code.myExample.example1;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PrinterServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//        System.out.println("PrinterServerHandler's thread:"+Thread.currentThread());
        System.out.println("print: "+msg.trim());
//        ctx.executor().execute(()-> System.out.println("PrinterServerHandler's added task 1 thread:"+Thread.currentThread()));
//        ctx.channel().eventLoop().execute(()-> System.out.println("PrinterServerHandler's added task 2 thread:"+Thread.currentThread()));
        ctx.writeAndFlush(msg.trim());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error exit!");
        ctx.close();
    }
}
