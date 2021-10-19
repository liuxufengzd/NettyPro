package liu.code.nio.groupChat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

public class ChatServer {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private int bufferSize = 1024;

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public ChatServer(int port) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        Set<SelectionKey> selectedKeys;
        while (true) {
            try {
                if (selector.select(1000) == 0) {
                    Thread.sleep(1000);
                    continue;
                }
                selectedKeys = selector.selectedKeys();
                selectedKeys.forEach(selectionKey -> {
                    if (selectionKey.isAcceptable()) {
                        try {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
                            System.out.println(socketChannel.getRemoteAddress() + ":上线了!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (selectionKey.isReadable()) {
                        SocketChannel socketChannel = null;
                        ByteBuffer buffer;
                        try {
                            socketChannel = (SocketChannel) selectionKey.channel();
                            buffer = (ByteBuffer) selectionKey.attachment();
                            socketChannel.read(buffer);
                            String msg = socketChannel.getRemoteAddress() + " 说话：" + new String(buffer.array(),0,buffer.position());
                            buffer.clear();
                            System.out.println(msg);
                            transferToOtherClients(selectionKey, msg);
                        } catch (IOException e) {
                            try {
                                System.out.println(socketChannel.getRemoteAddress() + " 离线了!");
                                selectionKey.cancel();
                                socketChannel.close();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    }
                });
                selectedKeys.clear();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void transferToOtherClients(SelectionKey key, String msg) {
        Set<SelectionKey> selectionKeys = selector.keys();
        selectionKeys.forEach(selectionKey -> {
            if (key != selectionKey && serverSocketChannel != selectionKey.channel()) {
                SocketChannel channel = (SocketChannel) selectionKey.channel();
                ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                try {
                    buffer.put(msg.getBytes());
                    buffer.flip();
                    channel.write(buffer);
                    buffer.clear();
                    System.out.println("服务器已成功转发信息给:" + channel.getRemoteAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
