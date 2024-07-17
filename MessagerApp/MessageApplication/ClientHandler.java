import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ClientHandler implements Runnable {
    public static Map<String, List<String>> getUserInboxes() {
        return userInboxes;
    }

    public static void setUserInboxes(Map<String, List<String>> userInboxes) {
        ClientHandler.userInboxes = userInboxes;
    }

    public static Map<String, String> getUserCredentials() {
        return userCredentials;
    }

    public static void setUserCredentials(Map<String, String> userCredentials) {
        ClientHandler.userCredentials = userCredentials;
    }

    private static Map<String, List<String>> userInboxes = new HashMap<>();
    private static Map<String, String> userCredentials = new HashMap<>();
    private Socket socket;
    private PrintWriter out;

    private BufferedReader in;

    private String authenticatedUser = null;

    public ClientHandler(){

    }
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Komut alındı : " + inputLine);
                String[] tokens = inputLine.split(" ", 3);
                String command = tokens[0].toUpperCase();

                switch (command) {
                    case "AUTH":
                        handleAuth(tokens);
                        break;
                    case "SEND":
                        handleSend(tokens);
                        break;
                    case "LIST":
                        handleList();
                        break;
                    case "READ":
                        handleRead(tokens);
                        break;
                    case "QUIT":
                        handleQuit();
                        break;
                    default:
                        out.println("UNKNOWN COMMAND");
                        break;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleQuit() {
        out.println("QUIT SUCCESS");
    }

    private void handleRead(String[] tokens) {
    }

    private void handleList() {
    }

    private void handleSend(String[] tokens) {
        if(authenticatedUser==null){
            out.println("Error : not authenticated");
            return;
        }
        if(tokens.length!=3){
            out.println("Error: Invalid message format");
            return;
        }
        String sender = tokens[1];
        String message= tokens[2];
        userInboxes.putIfAbsent(sender,new ArrayList<>());
        userInboxes.get(sender).add(message);
        out.println("SEND SUCCESS");

    }

    private void handleAuth(String[] tokens) {
        if (tokens.length != 3) {
            out.println("invalid auth format");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];

        if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
            authenticatedUser = username;
            out.println("AUTH SUCCESS");
        } else {
            out.println("AUTH FAILURE");
        }
    }

}
