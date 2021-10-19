package liu.code.nio;

import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class ScatteringAndGatheringTest {
    public static void main(String[] args) throws Exception {
        // ServerSocketChannelImpl contains ServerSocket socket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // Binds this socket to the specified local IP address and port number
        serverSocketChannel.socket().bind(new InetSocketAddress(8001));
        // create Buffer Array
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);
        // SocketChannelImpl contains Socket socket
        // Unlike the FileChannel can only read or write(bind with Stream),SocketChannel can read and write
        SocketChannel socketChannel = serverSocketChannel.accept();

        int messageLength = 8;
        while (true) {
            int byteRead = 0;
            while (byteRead < messageLength) {
                // can scatter the data to the Buffers automatically
                byteRead += socketChannel.read(byteBuffers);
                System.out.println("byteRead:" + byteRead);
                Arrays.asList(byteBuffers).stream().map(buffer -> "position:" + buffer.position() + "\t" + "limit:" + buffer.limit())
                        .forEach(System.out::println);
            }
            Arrays.asList(byteBuffers).forEach(Buffer::flip);
            int byteWrite = 0;
            while (byteWrite < messageLength) {
                // can gather the data of the Buffers automatically
                byteWrite += socketChannel.write(byteBuffers);
            }
            Arrays.asList(byteBuffers).forEach(Buffer::clear);
            System.out.println("byteRead:=" + byteRead + " byteWrite=" + byteWrite + ", messagelength" + messageLength);
        }
    }
}
