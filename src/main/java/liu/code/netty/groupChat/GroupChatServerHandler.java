package liu.code.netty.groupChat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {
    // GlobalEventExecutor.INSTANCE是一个全局事件执行器，单例
    // ConcurrentMap管理Channels
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // addXXX()调用后被执行，因为连接建立时会创建channel和pipeline并添加该handler
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // Group会将其管理的所有Channel进行遍历并发送数据，不需要自己遍历了，方便使用
        channelGroup.writeAndFlush("[client:]"+channel.remoteAddress()+" attends the chat "+sdf.format(new Date())+"\n");
        channelGroup.add(channel);
    }
    // ChannelPipeline remove(ChannelHandler handler);
    // 如果客户端非正常关闭，则服务器端无法感知，需要利用发送心跳包的机制进行监测
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[client:]"+channel.remoteAddress()+" leaves the chat "+sdf.format(new Date())+"\n");
        System.out.println("channelGroup size:"+channelGroup.size());
    }

    // Only fire a channelActive if the channel has never been connected and registered
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+" is active!");
    }
    // Request to disconnect from the remote peer
    // disconnect() 调用
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+" is inactive!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel ch = ctx.channel();
        channelGroup.forEach(channel -> {
            if (ch != channel){
                channel.writeAndFlush("[客户]" + channel.remoteAddress() + " 发送了消息" + msg + "\n");
            }
        });
    }
}
