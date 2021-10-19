package liu.code.netty.webSocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

//这里 TextWebSocketFrame 类型，表示一个文本帧(frame)
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器收到消息:"+msg.text());
        ctx.writeAndFlush(new TextWebSocketFrame("服务时间："+ LocalDateTime.now()+":"+msg.text()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //id 表示唯一的值，LongText 是唯一的 ShortText 不是唯一
        Channel channel = ctx.channel();
        System.out.println("handlerAdded 被调用" + channel.id().asLongText());
        System.out.println("handlerAdded 被调用" + channel.id().asShortText());
        // Server利用全双工长连接WebSocket应用层协议向client定时推送消息
        new Thread(() -> {
           while (true){
               try {
                   Thread.sleep(1000);
                   ctx.writeAndFlush(new TextWebSocketFrame("Server is active..."));
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
        }).start();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved 被调用" + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生 " + cause.getMessage());
        ctx.close(); //关闭连接
    }
}
