package liu.code.dubboRpc.client;

import liu.code.dubboRpc.client.service.HelloService;

public class ClientStart {
    public static void main(String[] args) {
        NettyClient customer = new NettyClient("127.0.0.1", 8001);
        String msg = "RPC_HEAD";
        // 获得代理对象
        HelloService proxy = (HelloService) customer.getBean(HelloService.class, msg);
        System.out.println(proxy.sayHello("good morning"));
        System.out.println(proxy.sayYou("ddd"));
        System.out.println(proxy.sayYou2("lxf","oxm"));
    }
}
