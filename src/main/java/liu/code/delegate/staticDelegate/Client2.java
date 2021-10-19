package liu.code.delegate.staticDelegate;

public class Client2 implements Rent {
    @Override
    public void rent() {
        System.out.println("client2 rent...");
    }

    @Override
    public void sign() {
        System.out.println("client2 sign...");
    }
}
