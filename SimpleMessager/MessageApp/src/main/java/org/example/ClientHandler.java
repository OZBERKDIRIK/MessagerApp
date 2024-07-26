package org.example;


import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;
import java.util.*;


class ClientHandler  implements Runnable {


    List<List<String>> sendLıstView = new ArrayList<>();
    List<List<String>> reciveLıstView = new ArrayList<>();
    List<String> messageReadView = new ArrayList<>();
    private Person person = new Person();
    private Socket socket;
    private PrintWriter out;

    private BufferedReader in;

    private Message message;
    private Gson gson = new Gson();
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
                    case "LIST": //klasör
                        handleList();
                        break;
                    case "READ":
                        handleRead(tokens);
                        break;
                    case "QUIT":
                        handleQuit();
                        break;
                    case "REGISTER":
                        handleRegister(tokens);
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
        String messageID = tokens[1];
        String readMessage="";
        if(person.getAuthenticatedUser()!=null){
                for(Message msg : Message.getMessageList()){
                    if(msg.messageId.equals(messageID)){
                        messageReadView.add("Gönderici: "+msg.sender);
                        messageReadView.add("Alıcı : "+msg.reciever);
                        messageReadView.add("Konu : "+msg.subtopic);
                        messageReadView.add("İçerik : "+msg.content);
                    }
                }
            }
        readMessage=gson.toJson(messageReadView);
        out.println(readMessage);
        messageReadView.clear();

        }

    private void handleList() {
        String reciveJSON="";
        String sendJSON="";
        String messageSender = person.getAuthenticatedUser();
            if (person.getAuthenticatedUser().equals(messageSender)) {
                for (Message msg : Message.getMessageList()) {
                    if (messageSender.equals(msg.sender)) {
                        message=msg.senderFile.get(messageSender);
                        message.sendLıst.add("Gönderilen Kutusu -----> ");
                        message.sendLıst.add("Message Id: " + message.messageId);
                        message.sendLıst.add("Gönderen : "+message.sender);
                        message.sendLıst.add("Alıcı : " + message.reciever);
                        message.sendLıst.add("Konu : "+message.subtopic);
                        sendLıstView.add(message.sendLıst);
                        sendJSON =gson.toJson(sendLıstView);

                    }
                        if(person.getUserID().contains(msg.reciever.substring(0,3) + person.getUserCredentials().get(msg.reciever).substring(0,3))){
                            message=message.reciverFile.get(msg.reciever);
                            message.reciveLıst.add("Gelen Kutusu ----> ");
                            message.reciveLıst.add("Message ID: " + message.messageId);
                            message.reciveLıst.add("Gönderen : " +message.sender);
                            message.reciveLıst.add("Alıcı : " +message.reciever);
                            message.reciveLıst.add("Konu : " +message.subtopic);
                            reciveLıstView.add(message.reciveLıst);
                            reciveJSON = gson.toJson(reciveLıstView);
                        }
                }
            }
            out.println(sendJSON + "\n\n"+reciveJSON);
        }






    private void handleSend(String [] tokens) {

        String sender = person.getAuthenticatedUser();
        String reciver=tokens[1];
        String [] topicAndContent = tokens[2].split(" ",2);
        String subtopic = topicAndContent[0];
        String content =topicAndContent[1];


        message = new Message(
                sender,
                reciver,
                subtopic,
                 content);

        System.out.println("authenticated User : " + sender);
        if(!person.getAuthenticatedUser().equals(sender)){
            out.println("ERROR: KULLANICI GİRİŞ YAPMADI");
            return;
        }
        if(tokens.length>3){
            out.println("ERROR: GEÇERLİ MESSAGE FORMATINDA DEĞİL");
            return;
        }
        if (person.getUserCredentials().containsKey(reciver)) {
              message.reciverFile.put(reciver,message);
              message.senderFile.put(sender,message);
              Message.setMessageList(message);
            out.println("SEND SUCCESS");

        }
        else{
            out.println("ERROR: ALICI BULUNAMADI");
        }

    }

    private void handleAuth(String[] tokens) {
        if (tokens.length != 3) {
            out.println("ERROR: GEÇERLİ AUTH FORMATINDA DEĞİL");
            return;
        }
        person.setUsername(tokens[1]);;
        person.setPassword(tokens[2]);

        System.out.println("UserCredantials: "+ person.getUserCredentials());
        System.out.println("Test: "+person.getUserCredentials().containsKey((person.getUsername())));
        System.out.println("Test -2  " +person.getUserCredentials().containsValue(person.getPassword()));
        if (person.getUserCredentials().containsKey(person.getUsername()) && person.getUserCredentials().containsValue(person.getPassword())) {
            person.setAuthenticatedUser(person.getUsername());
            person.setUserID();
            out.println("AUTH SUCCESS");
        } else {
            out.println("AUTH FAILURE");
        }
    }
    private void handleRegister(String[] tokens) {
        if (tokens.length != 3) {
            out.println("ERROR: GEÇERLİ REGISTER FORMATINDA DEGİL");
            return;
        }
        person.setUsername(tokens[1]);;
        person.setPassword(tokens[2]);
        person.setUserCredentials();
        System.out.println("UserCredentials: " + person.getUserCredentials());
        System.out.println("Username " + person.getUsername());
        System.out.println("Password : " +person.getPassword());
        if(person.getUserCredentials().containsKey(person.getUsername()) && person.getUserCredentials().containsValue(person.getPassword())) {
            out.println("REGISTER SUCCESS");
        }else{
            out.println("REGISTER FAILURE");
        }
    }

}

