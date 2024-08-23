package org.example;

import com.google.gson.annotations.SerializedName;

public class MessageLıst {

    @SerializedName("Mesaj ID : ")
    private String messageId;

    public String getSender() {
        return sender;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getReceiver() {
        return receiver;
    }

    @SerializedName("Gönderen : ")
    private String sender;

    @SerializedName("Alıcı : ")
    private String receiver;
    @SerializedName("Konu : ")
    private String subject;

    @SerializedName("İçerik : ")
    private String content;

    @Override
    public String toString() {
        return "\n"+"   { Mesaj ID : "
                + messageId + "\n"
                + "     Gönderen : "
                + sender +"\n"
                + "     Alıcı : "
                + receiver +"\n"
                + "     Konu : "
                + subject +"\n"
                +"  }" + "\n" ;

    }

    public String readMessage(){
        return "\n"+"   { Mesaj ID : "
                + messageId + "\n"
                + "     Gönderen : "
                + sender + "\n"
                + "     Alıcı : "
                + receiver + "\n"
                + "     Konu : "
                + subject + "\n"
                +"     İçerik :"
                +content + "\n"
                +"  }";

    }
}
