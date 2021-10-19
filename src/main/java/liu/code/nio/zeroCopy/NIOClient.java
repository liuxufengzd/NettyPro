package liu.code.nio.zeroCopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NIOClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1",8001));
        FileChannel fileChannel = new FileInputStream("apache-jmeter-5.3.zip").getChannel();
        long startTime = System.currentTimeMillis();
        long count = fileChannel.size()/8388608;
        long transferCount = 0;
        for (int i=0;i<=count;i++){
            // windows下，一次只能发送8M数据. transferTo 底层使用到零拷贝
            transferCount = fileChannel.transferTo(8388608*i, fileChannel.size(), socketChannel);
            System.out.println("发送的总的字节数 =" + transferCount + " 耗时:" + (System.currentTimeMillis() - startTime));
        }
        fileChannel.close();
        socketChannel.close();
    }
}
