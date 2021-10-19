package liu.code.netty.heart;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartHandler extends SimpleChannelInboundHandler<String> {
    private final Integer[] states = new Integer[]{0,0,0};
    private final Integer[] threshold = new Integer[]{5,3,2};
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Channel channel = ctx.channel();
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            switch (event.state()){
                case READER_IDLE:
                    System.out.println("读失败");
                    if ((++states[0]).equals(threshold[0])){
                        channel.close();
                        System.out.println(channel.remoteAddress()+":多次读失败，已关闭");
                    }
                    break;
                case WRITER_IDLE:
                    System.out.println("写失败");
                    if ((++states[1]).equals(threshold[1])){
                        channel.close();
                        System.out.println(channel.remoteAddress()+":多次写失败，已关闭");
                    }
                    break;
                case ALL_IDLE:
                    System.out.println("读或写失败");
                    if ((++states[2]).equals(threshold[2])){
                        channel.close();
                        System.out.println(channel.remoteAddress()+":多次读或写失败，已关闭");
                    }
                    break;
            }
        }
    }
}
