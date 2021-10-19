package liu.code.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws Exception {
        // 配置好serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8001));
        serverSocketChannel.configureBlocking(false);
        // 创建Selector对象
        Selector selector = Selector.open();
        // 将serverSocketChannel注册进Selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // keys(): Returns this selector's key set.A key is removed only after
        // it has been cancelled and its channel has been unregistered
        System.out.println("注册后的selectionkey 数量=" + selector.keys().size()); // 1

        Set<SelectionKey> selectedKeys;
        while (true) {
            // Selects a set of keys whose corresponding channels are ready for I/O operations.
            // blocking method to listen to requests. or selectNow() as non-blocking method to listen.
            if (selector.select(1000) == 0) {
                System.out.println("no connection comes...");
                Thread.sleep(1000);
                continue;
            }
            // Returns this selector's selected-key set
            selectedKeys = selector.selectedKeys();
            selectedKeys.forEach(selectionKey -> {
                if (selectionKey.isAcceptable()) {
                    try {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                        System.out.println("客户端连接成功 生成了一个 socketChannel " + socketChannel.hashCode());
                        System.out.println("客户端连接后 ，注册的selectionkey 数量=" + selector.keys().size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (selectionKey.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                    try {
                        socketChannel.read(buffer);
                        System.out.println("from 客户端：" + new String(buffer.array()));
                        System.out.println("客户端连接后 ，注册的selectionkey 数量=" + selector.keys().size());
                        buffer.clear();
                    } catch (IOException e) {
                        try {
                            System.out.println("client has left...");
                            // cancel: remove from the keys set
                            selectionKey.cancel();
                            socketChannel.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            });
            // 完成一次处理后，将之清空，准备下次select
            selectedKeys.clear();
        }
    }
}
