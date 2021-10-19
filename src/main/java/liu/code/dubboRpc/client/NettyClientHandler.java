package liu.code.dubboRpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Callable;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> implements Callable<String> {
    private String callMsg;
    private ChannelHandlerContext context;
    private String result;

    public void setCallMsg(String callMsg) {
        this.callMsg = callMsg;
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("response is returned from server");
        result = msg;
        notify();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handler is activated...");
        context = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public synchronized String call() throws Exception {
        System.out.println("sent message...");
        context.writeAndFlush(callMsg);
        wait();
        System.out.println("result is returned...");
        return result;
    }
}
