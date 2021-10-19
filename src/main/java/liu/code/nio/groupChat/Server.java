package liu.code.nio.groupChat;

public class Server {
    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer(8001);
        chatServer.listen();
    }
}
