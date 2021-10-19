package liu.code.myExample.example5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class HttpServerHandler extends SimpleChannelInboundHandler<DefaultHttpRequest> {
    private static int count;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultHttpRequest msg) throws Exception {
//        System.out.println("method:"+msg.method());
//        System.out.println("uri:"+msg.uri());
//        msg.headers().entries().forEach(entry-> System.out.println("key:"+entry.getKey()+"-->value:"+entry.getValue()));
//        System.out.println(msg);

        String response = "Server received!---"+count++;
        ByteBuf byteBuf = Unpooled.copiedBuffer(response.getBytes(CharsetUtil.UTF_8));
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        HttpHeaders headers = httpResponse.headers();
        headers.set(HttpHeaderNames.CONTENT_TYPE,"plain/text");
        headers.set(HttpHeaderNames.CONTENT_LENGTH,byteBuf.readableBytes());
        ctx.writeAndFlush(httpResponse);
    }
}
