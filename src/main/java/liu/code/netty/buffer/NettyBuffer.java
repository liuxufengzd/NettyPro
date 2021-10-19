package liu.code.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class NettyBuffer {
    public static void main(String[] args) {
        // maxCapacity: 最大值默认为2147483647，writerIndex>capacity时会自动适当增长capacity
//        ByteBuf buffer = Unpooled.buffer(10,20);
        String msg = "hello,world";
        ByteBuf buffer = Unpooled.copiedBuffer(msg, StandardCharsets.UTF_8);
        System.out.println(buffer.maxCapacity());
        for (int i = 0; i < 5; i++) {
            buffer.writeByte(i);
        }
        System.out.println(buffer.capacity());
        // IndexOutOfBoundsException
//        for (int i=0;i<buffer.capacity();i++){
//            System.out.println(buffer.readByte());
//        }
        while (buffer.readerIndex()<buffer.writerIndex())
            System.out.println(buffer.readByte());
        System.out.println(buffer.writerIndex());
        System.out.println(buffer.readableBytes());
        System.out.println(buffer.writableBytes());
        if (buffer.hasArray())
            System.out.println(new String(buffer.array(), StandardCharsets.UTF_8));
        System.out.println(buffer.arrayOffset());
        System.out.println(buffer.getCharSequence(0, 4, StandardCharsets.UTF_8));
    }
}
