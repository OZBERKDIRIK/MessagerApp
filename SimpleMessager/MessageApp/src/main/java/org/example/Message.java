package org.example;

import java.util.*;

public class Message {

    String sender;

    String reciever;

    String subtopic;

    String content;

    String messageId;
    List<String> messageID = new ArrayList<>();
    List<String> sendLıst = new ArrayList<>();
    List<String> reciveLıst = new ArrayList<>();

    static Map<String, Message> reciverFile = new HashMap<>();

     Map<String, Message> senderFile=new HashMap<>();

    private static List<Message> messageList = Collections.synchronizedList(new ArrayList<>());

    public static List<Message> getMessageList() {
        return messageList;
    }

    public static void setMessageList(Message message) {
        Message.messageList.add(message);
    }



    private String generateUniqueID()
    {
        String id = UUID.randomUUID().toString();
        this.messageID.add(id);
        return id;
    }

    private void setMessageId(String messageId){
        this.messageId=messageId;
    }
   public Message(String sender , String reciver , String subtopic ,String content){
        generateUniqueID();
        setMessageId(generateUniqueID());
        this.sender=sender;
        this.reciever=reciver;
        this.subtopic=subtopic;
        this.content=content;
   }
}
