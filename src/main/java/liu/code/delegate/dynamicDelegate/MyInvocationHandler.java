package liu.code.delegate.dynamicDelegate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyInvocationHandler implements InvocationHandler {
    private final Object target;

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    // 代理类的处理：调用被代理类的核心方法，增强功能
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        doBefore();
        Object res = method.invoke(target, args);
        if (res instanceof Integer){
            int result = (Integer) res + 1;
            System.out.println("handle the computed result with adding 1");
            doAfter();
            return result;
        }else
            return res;
    }

    private void doBefore(){
        System.out.println("handle before...");
    }

    private void doAfter(){
        System.out.println("handle after...");
    }
}
