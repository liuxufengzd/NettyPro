package liu.code.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class FileChannelTest {
    public static void main(String[] args) throws IOException {
        // write to a file
        FileOutputStream stream = new FileOutputStream("test.txt");
        // Get the FileChannelImpl Object (here is a writable one)
        // The stream contains the Channel
        FileChannel fileChannel = stream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String data = "hello,刘旭峰";
        buffer.put(data.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        fileChannel.write(buffer);
        stream.close();

        // read from a file
        File file = new File("test.txt");
        FileInputStream inputStream = new FileInputStream((file));
        FileChannel channel = inputStream.getChannel();
        ByteBuffer buffer1 = ByteBuffer.allocate((int) file.length());
        channel.read(buffer1);
        // return the byte[] of the buffer
        System.out.println(new String(buffer1.array()));

        // copy a file using one buffer
        FileInputStream fileInputStream = new FileInputStream("1.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("2.txt");
        FileChannel readChannel = fileInputStream.getChannel();
        FileChannel writeChannel = fileOutputStream.getChannel();
//        readChannel.transferTo(0,readChannel.size(),writeChannel); can replace all the following code
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (true){
            byteBuffer.clear(); // if not, length will be 0 forever
            // BIO uses Stream and array to read/write directly but NIO uses Channel and Buffer(enhanced array)
            int length = readChannel.read(byteBuffer);
            if (length == -1)
                break;
            byteBuffer.flip();
            writeChannel.write(byteBuffer);
        }
        fileInputStream.close();
        fileOutputStream.close();

        // transferFrom/transferTo (simpler)
        FileChannel srcChannel = new FileInputStream("lxf.png").getChannel();
        FileChannel destChannel = new FileOutputStream("lwh.png").getChannel();
        destChannel.transferFrom(srcChannel,0,srcChannel.size());
//        srcChannel.transferTo(0,srcChannel.size(),destChannel);
        srcChannel.close();
        destChannel.close();

        // MappedByteBuffer
        // 可读写的文件对象
        RandomAccessFile ra = new RandomAccessFile("1.txt", "rw");
        FileChannel channel1 = ra.getChannel();
        // 从0开始的5个字节可以修改（即在Direct Memory中映射了多少个字节）
        // 实现类为DirectByteBuffer，采用了Direct Memory (应用可直接访问的堆外内存，不用buffer中转),效率高但销毁创建代价高，适用于经常写的情况
        MappedByteBuffer mappedByteBuffer = channel1.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        mappedByteBuffer.put(0, (byte) 'H');
        mappedByteBuffer.put(3, (byte) '9');
        ra.close();
    }
}
