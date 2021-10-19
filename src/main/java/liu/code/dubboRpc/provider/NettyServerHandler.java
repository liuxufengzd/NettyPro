package liu.code.dubboRpc.provider;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NettyServerHandler extends SimpleChannelInboundHandler<String> {
    private String servicePath = "liu.code.dubboRpc.provider.service.";
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("read data...");
        if (msg.startsWith("RPC_HEAD")){
            String service = msg.substring(msg.indexOf("#")+1, msg.lastIndexOf("#"));
            String[] strings = service.split("\\.");
            String calledService = strings[0];
            String calledMethod = strings[1];
            String[] args = strings[2].split(",");

            Class<?> clazz = Class.forName(servicePath + calledService + "Impl");
            Method[] methods = clazz.getMethods();
            for (Method method:methods){
                if (method.getName().equals(calledMethod)){
                    try {
                        String res = (String) method.invoke(clazz.newInstance(), args);
                        ctx.writeAndFlush(res);
                        return;
                    } catch (IllegalArgumentException ignored) {}
                }
            }
            ctx.writeAndFlush("no such method has been found!");
        }else
            ctx.writeAndFlush("not valid call!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
