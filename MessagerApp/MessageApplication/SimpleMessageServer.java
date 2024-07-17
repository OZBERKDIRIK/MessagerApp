import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SimpleMessageServer {
    public  static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Sunucu 12345 portunda başlatıldı");
        Map<String , String> userCredantials = new HashMap<String ,String>();
        userCredantials.put("Özberk","1234");
        ClientHandler.setUserCredentials(userCredantials);

        while(true){
            Socket clientSocket =serverSocket.accept();
            System.out.println("Yeni bağlantı kabul edildi: " + clientSocket.getInetAddress());
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }
}
