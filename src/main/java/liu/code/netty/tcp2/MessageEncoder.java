package liu.code.netty.tcp2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<MessagePackage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePackage msg, ByteBuf out) throws Exception {
        // 写入buffer中会自动向channel中发送数据出去
        out.writeInt(msg.getLength());
        out.writeBytes(msg.getContent());
    }
}
