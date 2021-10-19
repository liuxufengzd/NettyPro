package liu.code.dubboRpc.provider;

public class ServerStart {
    public static void main(String[] args) {
        new NettyServer().initServer(8001);
    }
}
