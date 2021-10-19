package liu.code.myExample.example6.client;

import java.net.InetSocketAddress;

public class ClientStarter {
    public static void main(String[] args) {
        CallerClient callerClient = new CallerClient(new InetSocketAddress("127.0.0.1", 8080));
        CommonService proxy = (CommonService) callerClient.getProxy(CommonService.class);

        int sum = proxy.calSum(2, 4);
        String string = proxy.upperString("Liu Xufeng");
        System.out.println(sum + ":" + string);
    }
}
