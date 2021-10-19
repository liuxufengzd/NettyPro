package liu.code.nio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class BasicBufferTest {
    public static void main(String[] args) {
        IntBuffer buffer = IntBuffer.allocate(5);
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put(i);
        }
        buffer.flip();
        buffer.position(1);
        buffer.limit(4);
        while (buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }
        buffer.clear();
        System.out.println(buffer.get(2));
        System.out.println(buffer.isReadOnly());

        // ----------------------
        System.out.println("========================");
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);
        byteBuffer.putChar('L');
        byteBuffer.putDouble(12.99);
        byteBuffer.putInt(12);
        byteBuffer.flip();
        while (byteBuffer.hasRemaining())
            System.out.println(byteBuffer.get()); // 只能得到二进制数据
        byteBuffer.position(0);
        System.out.println("==================");
        System.out.println(byteBuffer.getChar()); // 类型顺序必须必须一致
        System.out.println(byteBuffer.getDouble());
        System.out.println(byteBuffer.getInt());

        // ----------------------
        System.out.println("========================");
        byteBuffer.clear();
        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
        System.out.println(readOnlyBuffer.isReadOnly());
        readOnlyBuffer.putInt(12); //java.nio.ReadOnlyBufferException
    }
}
