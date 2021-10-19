package liu.code.netty.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Set;
// 主从Reactor模式
//Reactor模式在IO读写数据时还是在同一个线程中实现的，即使使用多个Reactor机制的情况下，那些共享一个Reactor的Channel如果出现一个长时间的数据读写，
//会影响这个Reactor中其他Channel的相应时间，比如在大文件传输时，IO操作就会影响其他Client的相应时间.
public class MainReactor implements Runnable {
    private Selector mainSelector;
    private ServerSocketChannel serverSocketChannel;
    private SubReactor[] subReactors;
    private int index;

    public MainReactor(int port, int tNum) throws IOException {
        mainSelector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.register(mainSelector, SelectionKey.OP_ACCEPT, new Acceptor());
        subReactors = new SubReactor[tNum];
        for (int i=0;i<tNum;i++)
            subReactors[i] = new SubReactor();
        Arrays.asList(subReactors).forEach(subReactor -> new Thread(subReactor).start());
    }
    public MainReactor(int port) throws IOException {
        this(port, 3);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (mainSelector.select(1000) == 0) {
                    Thread.sleep(1000);
                    continue;
                }
                Set<SelectionKey> selectedKeys = mainSelector.selectedKeys();
                selectedKeys.forEach(selectionKey -> {
                    Acceptor acceptor = (Acceptor) selectionKey.attachment();
                    SocketChannel socketChannel = acceptor.accept(selectionKey);
                    try {
                        System.out.println("connected: "+socketChannel.getRemoteAddress());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dispatch(socketChannel);
                });
                selectedKeys.clear();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void dispatch(SocketChannel socketChannel) {
        subReactors[choose()].register(socketChannel);
    }

    public int choose(){
        return (index++) % subReactors.length;
    }

    public class Acceptor {
        public SocketChannel accept(SelectionKey key) {
            if (key.isAcceptable()) {
                SocketChannel socketChannel = null;
                try {
                    socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    return socketChannel;
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
            return null;
        }
    }
}
