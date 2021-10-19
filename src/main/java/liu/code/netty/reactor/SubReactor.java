package liu.code.netty.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

public class SubReactor implements Runnable {
    private Selector selector;
    private Handler[] handlers;
    private int index;

    public SubReactor() {
        try {
            selector = Selector.open();
            handlers = new Handler[5];
            for (int i=0;i<5;i++)
                handlers[i] = new Handler();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        new Thread(new Listener()).start();
        while (true) {
            try {
                System.out.println(Thread.currentThread().getName()+": Waiting for connection...");
                if (selector.select() == 0) // blocking
                    continue;
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                selectedKeys.forEach(key -> {
                    Handler handler = handlers[choose()];
                    handler.read(key); // blocking
                });
                selectedKeys.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void register(SocketChannel socketChannel) {
        try {
            selector.wakeup();
            socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    private int choose() {
        return (index++) % handlers.length;
    }

    public class Handler {
        private ExecutorService threadPool = Executors.newCachedThreadPool();
        // 多线程环境下必须使用 ConcurrentXXX
        private Map<Future<String>, SelectionKey> map = new ConcurrentHashMap<>();

        public void read(SelectionKey key) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            try {
                socketChannel.read(buffer);
                process(new String(buffer.array(), StandardCharsets.UTF_8), key);
                buffer.clear();
            } catch (IOException e) {
                try {
                    key.cancel();
                    socketChannel.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        public void process(String msg, SelectionKey key) {
            map.put(threadPool.submit(new Worker(msg)), key);
        }

        public void send(String msg, SelectionKey key) {
            try {
                ByteBuffer buffer = (ByteBuffer) key.attachment();
                buffer.put(msg.getBytes());
                buffer.flip();
                SocketChannel channel = (SocketChannel) key.channel();
                channel.write(buffer);
                buffer.clear();
                System.out.println(Thread.currentThread().getName()+": send back to client");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Listener implements Runnable {
        @Override
        public void run() {
            while (true) {
                for (Handler handler : handlers) {
                    Iterator<Map.Entry<Future<String>, SelectionKey>> iterator = handler.map.entrySet().iterator();
                    while (iterator.hasNext()){
                        Map.Entry<Future<String>, SelectionKey> entry = iterator.next();
                        Future<String> future = entry.getKey();
                        if (future.isDone()){
                            try {
                                handler.send(future.get(),entry.getValue());
                                iterator.remove();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        }
    }
}
