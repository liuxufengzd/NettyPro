package liu.code.netty.reactor.test;

import liu.code.netty.reactor.MainReactor;

import java.io.IOException;

public class ReactorTest {
    public static void main(String[] args) throws IOException {
        new Thread(new MainReactor(8001)).start();
    }
}
