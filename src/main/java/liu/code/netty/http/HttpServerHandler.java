package liu.code.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/*
说明
1. SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter子类,allows to explicit only handle a specific type of messages
2. HttpObject 客户端和服务器端相互通讯的数据被封装成 HttpObject
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        System.out.println("对应的channel=" + ctx.channel() + " pipeline=" + ctx
                .pipeline() + " 通过pipeline获取channel" + ctx.pipeline().channel());
        System.out.println("当前ctx的handler= "+ctx.handler());
        if (msg instanceof HttpRequest){
            System.out.println("ctx 类型="+ctx.getClass());
            System.out.println("pipeline hashcode" + ctx.pipeline().hashCode() + " HttpServerHandler hash=" + this.hashCode());
            System.out.println("msg 类型=" + msg.getClass());
            System.out.println("客户端地址" + ctx.channel().remoteAddress());
            HttpRequest httpRequest = (HttpRequest)msg;
            //获取uri, 过滤指定的资源
            URI uri = new URI(httpRequest.uri());
            String path = uri.getPath();
            if ("/favicon.ico".equals(path)){
                System.out.println("请求了 favicon.ico, 不做响应");
                return;
            }
            //回复信息给浏览器 [http协议]
            String responseMsg = "hello, 我是服务器";
            ByteBuf content = Unpooled.copiedBuffer(responseMsg, StandardCharsets.UTF_8);
            //构造一个http的相应，即 httpResponse
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,content);
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
            ctx.writeAndFlush(httpResponse);
        }
    }
}
