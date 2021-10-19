package liu.code.myExample.example4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int contentLength = in.readInt();
        byte[] bytes = new byte[contentLength];
        in.readBytes(bytes);
        out.add(new MessagePackage(contentLength, bytes));
    }
}
