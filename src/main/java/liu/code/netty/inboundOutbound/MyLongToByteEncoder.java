package liu.code.netty.inboundOutbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
//编写 Encoder 是要注意传入的数据类型和处理的数据类型一致
public class MyLongToByteEncoder extends MessageToByteEncoder<Long> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
        System.out.println("encoder...:"+msg);
        out.writeLong(msg);
    }
}
