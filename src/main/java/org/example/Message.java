package org.example;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.*;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    @Expose
    String sender;

    @Expose
    String reciever;

    @Expose
    String subtopic;

    String content;

    @Expose
    String messageId;

    @Expose
    List<String> messageID = new ArrayList<>();

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
