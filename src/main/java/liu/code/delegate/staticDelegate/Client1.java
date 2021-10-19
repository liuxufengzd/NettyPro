package liu.code.delegate.staticDelegate;

public class Client1 implements Rent {
    @Override
    public void rent() {
        System.out.println("client1 rent...");
    }

    @Override
    public void sign() {
        System.out.println("client1 sign...");
    }
}
