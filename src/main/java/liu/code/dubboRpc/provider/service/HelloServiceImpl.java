package liu.code.dubboRpc.provider.service;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String msg) {
        System.out.println("server received message:"+msg);
        if (msg!=null){
            return "server->"+msg;
        }else
            return "null message...";
    }

    @Override
    public String sayYou(String msg) {
        return "server you->"+msg;
    }

    @Override
    public String sayYou2(String msg1, String msg2) {
        return "server you->"+msg1+" and "+msg2;
    }


}
