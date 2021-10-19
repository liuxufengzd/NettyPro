package liu.code.delegate.staticDelegate;

public class Test {
    public static void main(String[] args) {
        Client1 client1 = new Client1();
        Client2 client2 = new Client2();
        new Proxy(client1).rent();
        new Proxy(client1).sign();
        new Proxy(client2).rent();
        new Proxy(client2).sign();
    }
}
