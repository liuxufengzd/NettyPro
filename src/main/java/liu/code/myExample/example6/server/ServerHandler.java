package liu.code.myExample.example6.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        ctx.writeAndFlush(handleMessage(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " is connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " is disconnected");
    }

    private String handleMessage(String msg) throws Exception {
        if (!msg.startsWith("RPC_HEAD")){
            return "Not a valid request!";
        }
        msg = msg.substring(msg.indexOf("#")+1, msg.lastIndexOf("#"));
        String[] strings = msg.split("\\.");
        String calledService = strings[0];
        String calledMethod = strings[1];
        String[] args = strings[2].split(",");
        Class<?> aClass = Class.forName("liu.code.myExample.example6.server."+calledService + "Impl");
        Method[] methods = aClass.getMethods();
        for (Method method:methods){
            if (method.getName().equals(calledMethod)){
                if (method.getReturnType().getTypeName().equals("java.lang.Integer")){
                    Integer[] integers = new Integer[args.length];
                    for (int i=0;i<args.length;i++){
                        integers[i] = Integer.parseInt(args[i]);
                    }
                    Integer result = (Integer)method.invoke(aClass.newInstance(), integers);
                    return result.toString();
                }
                return (String)method.invoke(aClass.newInstance(), args);
            }
        }
        return "No valid method has been found!";
    }
}
