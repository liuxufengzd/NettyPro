package liu.code.delegate.dynamicDelegate;

import java.lang.reflect.Proxy;

public class Test {
    public static void main(String[] args) {
        ComputeService service = new ComputeServiceImpl();
        ComputeService proxyInstance = (ComputeService) Proxy.newProxyInstance(service.getClass().getClassLoader(),
                service.getClass().getInterfaces(),
                new MyInvocationHandler(service));
        System.out.println(proxyInstance.compute(2));
    }
}
