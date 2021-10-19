package liu.code.myExample.example2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleHandler extends SimpleChannelInboundHandler<String> {
    private int dieIndex = 3;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        dieIndex = 3;
        if (!msg.startsWith(">>>alive"))
            System.out.println("From client->"+ctx.channel().remoteAddress()+": "+msg.trim());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String eventType;
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()){
                case READER_IDLE:
                    sendHeartbeat(ctx);
                    eventType = "read idle";
                    break;
                case WRITER_IDLE:
                    sendHeartbeat(ctx);
                    eventType = "write idle";
                    break;
                case ALL_IDLE:
                    sendHeartbeat(ctx);
                    eventType = "all idle";
                    break;
                default:
                    eventType = "nothing happened";
            }
            System.out.println(eventType);
        }
    }

    private void sendHeartbeat(ChannelHandlerContext ctx){
        ctx.writeAndFlush(">>>heartbeat"+dieIndex--);
        if (dieIndex < 0){
            ctx.close();
            System.out.println(ctx.channel().remoteAddress()+" is closed!");
        }
    }
}
