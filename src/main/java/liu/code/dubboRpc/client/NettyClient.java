package liu.code.dubboRpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {
    private final int port;
    private final String hostName;
    private int count;
    private NettyClientHandler nettyClientHandler;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public NettyClient(String hostName, int port) {
        this.port = port;
        this.hostName = hostName;
    }

    public Object getBean(Class<?> serviceClass, String protocalHead) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{serviceClass},
                (proxy, method, args) -> {
                    System.out.println(method.getReturnType().getTypeName());
                    System.out.println("调用方法" + (++count) + " 次");
                    // 第一次调用的时候初始化底层Netty
                    if (nettyClientHandler==null)
                        initClient();
                    String para = "";
                    for (Object arg : args)
                        para += arg+",";
                    para = para.substring(0, para.length()-1);
                    nettyClientHandler.setCallMsg(protocalHead+"#"+serviceClass.getSimpleName()+"."+method.getName()+"."+para+"#");
                    return executorService.submit(nettyClientHandler).get();
                });
    }

    private void initClient() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            nettyClientHandler = new NettyClientHandler();
                            pipeline.addLast(nettyClientHandler);
                        }
                    });
            bootstrap.connect(hostName, port).sync();
            System.out.println("connect succeed!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
