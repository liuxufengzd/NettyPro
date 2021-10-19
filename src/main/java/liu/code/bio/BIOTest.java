package liu.code.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOTest {
    public static void main(String[] args) throws IOException {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(8001);
        while (true){
            // blocked here if no request is accepted
            Socket socket = serverSocket.accept();
            threadPool.submit(()->handler(socket));
        }
    }

    public static void handler (Socket socket){
        try(InputStream inputStream = socket.getInputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            // read is blocked BIO method
            while ((length = inputStream.read(buffer))!=-1){
                System.out.println(new String(buffer,0,length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
