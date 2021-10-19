package liu.code.myExample.example6.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.SocketAddress;

public class CallerClient {
    private static final String RPC_HEAD = "RPC_HEAD";
    private static final EventExecutorGroup eventExecutors = new DefaultEventExecutorGroup(5);
    private ClientHandler clientHandler;
    private final SocketAddress socketAddress;
    private NioEventLoopGroup group;
    private ChannelFuture channelFuture;

    public CallerClient(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public void connect(SocketAddress socketAddress){
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        clientHandler = new ClientHandler();
                        ch.pipeline().addLast(new StringDecoder(),new StringEncoder());
                        ch.pipeline().addLast(eventExecutors,clientHandler);
                    }
                });
        try {
            channelFuture = bootstrap.connect(socketAddress).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() throws InterruptedException {
        channelFuture.channel().close().sync();
        group.shutdownGracefully();
    }

    public Object getProxy(Class<?> serviceClass){
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class<?>[]{serviceClass},
                (proxy,method,args)->{
                    String data = makeRequestString(serviceClass.getSimpleName(),method.getName(),args);
                    connect(socketAddress);
                    String result = clientHandler.sendMessage(data);
                    disconnect();
                    if (method.getReturnType().getTypeName().equals("java.lang.Integer"))
                        return Integer.parseInt(result);
                    return result;
                });
    }

    private String makeRequestString(String className, String methodName, Object[] args){
        StringBuilder para = new StringBuilder();
        for (Object arg:args)
            para.append(arg).append(",");
        return RPC_HEAD+"#"+className+"."+methodName+"."+para.substring(0,para.length()-1)+"#";
    }
}
