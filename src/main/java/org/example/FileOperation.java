package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperation {

    String userName = System.getProperty("user.name");

    String desktopPath = System.getProperty("user.home") + "\\Desktop";
    File messager = new File(desktopPath+"\\Message");
    File register =  new File(messager, "Kayıt.txt")  ;
    File auth =new File(messager,"Giriş.txt") ;

    File messageFile= new File(desktopPath+"\\Message\\Mesajlar");

    File allMessage = new File(messageFile,"Tüm Mesajlar.txt");

    File userMessage;
    File send ;
    File receive ;

}
