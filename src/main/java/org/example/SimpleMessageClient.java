package org.example;

import java.io.*;
import java.net.*;
import java.util.Scanner;
public class SimpleMessageClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            Scanner scanner = new Scanner(System.in);
            String userInput;

            System.out.println("Bağlantı kuruldu. Komutlarınızı girebilirsiniz.");

            while (true) {
                System.out.print("> ");
                userInput = scanner.nextLine();
                out.println(userInput);

                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    System.out.println(serverResponse);
                    if (!in.ready()) {
                        break;
                    }
                }
                if (userInput.equals("QUIT") || userInput.equals("quıt")) {
                    break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Bağnatı Hatası ");
        }
    }
}


