package org.example;

import java.io.Serializable;
import java.util.*;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    String sender;

    String reciever;

    String subtopic;

    String content;

    String messageId;
    List<String> messageID = new ArrayList<>();
    List<String> sendLıst = new ArrayList<>();
    List<String> reciveLıst = new ArrayList<>();


    private static List<Message> messageList = Collections.synchronizedList(new ArrayList<>());


    private String generateUniqueID() {
        String id = UUID.randomUUID().toString();
        this.messageID.add(id);
        return id;
    }

    private void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Message() {

    }

    public Message(String sender, String reciver, String subtopic, String content) {
        generateUniqueID();
        setMessageId(generateUniqueID());
        this.sender = sender;
        this.reciever = reciver;
        this.subtopic = subtopic;
        this.content = content;
    }
}
