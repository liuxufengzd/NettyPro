package liu.code.netty.reactor;

import java.util.concurrent.Callable;

public class Worker implements Callable<String> {
    private String msg;

    public Worker(String msg){
        this.msg = msg;
    }

    @Override
    public String call() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        msg = msg.toUpperCase();
        return msg;
    }
}
