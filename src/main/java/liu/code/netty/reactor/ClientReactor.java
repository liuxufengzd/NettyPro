package liu.code.netty.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;

public class ClientReactor implements Runnable {
    private SocketChannel socketChannel;
    private Selector selector;
    private InetSocketAddress socketAddress;
    private int bufferSize = 1024;
    private ByteBuffer buffer;

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public ClientReactor(String hostname, int port) {
        try {
            socketChannel = SocketChannel.open();
            selector = Selector.open();
            socketChannel.configureBlocking(false);
            buffer = ByteBuffer.allocate(bufferSize);
            socketChannel.register(selector, SelectionKey.OP_READ);
            socketAddress = new InetSocketAddress(hostname, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            if (!socketChannel.connect(socketAddress)) {
                while (!socketChannel.finishConnect())
                    System.out.println("因为连接需要时间，客户端不会阻塞，可以做其它工作..");
            }
            new Thread(() -> {
                while (true) {
                    readInfo();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            while (true) {
                System.out.print("message:");
                Scanner scanner = new Scanner(System.in);
                buffer.put(scanner.nextLine().getBytes());
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readInfo() {
        try {
            if (selector.selectNow() == 0)
                return;
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            selectedKeys.forEach(selectionKey -> {
                if (selectionKey.isReadable()) {
                    try {
                        socketChannel.read(buffer);
                        System.out.println();
                        System.out.println(new String(buffer.array()));
                        System.out.print("message:");
                        buffer.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            selectedKeys.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
