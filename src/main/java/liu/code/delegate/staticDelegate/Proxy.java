package liu.code.delegate.staticDelegate;

public class Proxy implements Rent {
    private final Rent client;

    public Proxy(Rent client) {
        this.client = client;
    }

    @Override
    public void rent() {
        beforeHandle();
        client.rent();
        afterHandle();
    }

    @Override
    public void sign() {
        beforeHandle();
        client.sign();
        afterHandle();
    }

    private void beforeHandle(){
        System.out.println("handle rent...before");
    }

    private void afterHandle(){
        System.out.println("handle rent...after");
    }


}
