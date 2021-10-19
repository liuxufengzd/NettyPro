package liu.code.nio.groupChat;

public class Client {
    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient("127.0.0.1", 8001);
        chatClient.start();
    }
}
