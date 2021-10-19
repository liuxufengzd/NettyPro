package liu.code.netty.reactor.test;

import liu.code.netty.reactor.ClientReactor;

public class ClientTest {
    public static void main(String[] args) {
        new Thread(new ClientReactor("127.0.0.1",8001)).start();
    }
}
