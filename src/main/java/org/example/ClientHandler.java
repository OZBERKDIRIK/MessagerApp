package org.example;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

class ClientHandler extends FileOperation implements Runnable {

    private JsonObject jsonObject = new JsonObject();
    private Person person = new Person();
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Message message = new Message();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

    /**
     * Server ile Client bağlantısını kapatmak için kullanılır.
     */
    private void handleQuit() {
        out.println("QUIT SUCCESS");
    }

    /**
     *
     * @param tokens ---> 2 parametre alır
     *               Birincisi read komutu
     *               İkincisi mesajın ID degeri
     * Mesajın ID degeri tüm mesajlar arasında aranır.
     * Eşleşme olduğunda deger ekrana yazdırılır.
     * @throws FileNotFoundException
     */
    private void handleRead(String[] tokens) throws FileNotFoundException {
        if(tokens.length!=2){
            out.println("UYGUN READ FORMATINDA DEĞİL ");
            return ;
        }
        String messageID = tokens[1];
        String id = "";
        List<JsonObject> jsonMessage = readMessage();
        for (JsonObject json : jsonMessage) {
            id = (json.get("Mesaj ID : ").toString().replace("\\", "").replace("\"", ""));
            if (messageID.equals(id)) {
                out.println(json);
                break;
            }else{
                out.println("MESAJ BULUNAMADI");
            }
        }
    }

    /**
     * Mesajları listelemeye yarar
     * Her kullanıcı giriş yaptığında onun için bir userID oluşturulur.
     * Bu userID ile birlikte o anda giriş yapmış kişinin gönderen ya da gönderici olduğu tespit edilir.
     * Mesajlar klasörün altında username ile oluşturulmuş klasör altında Gelen.txt ve Gönderilen.txt olarak yazılır.
     * @throws IOException
     */
    private void handleList() throws IOException {
        String reciveJSON = "";
        String sendJSON = "";
        if (!person.getAuthenticatedUser().isEmpty() && allMessage.exists()) {
            List<JsonObject> jsonMessage = readMessage();
            for (JsonObject json : jsonMessage) {
                String reciveID = json.get("Alıcı : ").toString().replace("\"", "").substring(0, 3) + setUsers().get(json.get("Alıcı : ").toString().replace("\"", "")).substring(0, 3);
                String senderID = json.get("Gönderen : ").toString().replace("\"", "").substring(0, 3) + setUsers().get(json.get("Gönderen : ").toString().replace("\"", "")).substring(0, 3);
                System.out.println("Recive ID : " + reciveID);
                System.out.println("Sender ID : " + senderID);
                System.out.println(person.getUserID().contains(reciveID));
                if (person.getUserID().contains(senderID)) {
                    message.sendLıst.add("Mesaj ID : " + json.get("Mesaj ID : "));
                    message.sendLıst.add("Gönderen : " + json.get("Gönderen : "));
                    message.sendLıst.add("Alıcı : " + json.get("Alıcı : "));
                    message.sendLıst.add("Konu : " + json.get("Konu : "));
                    sendJSON = gson.toJson(message.sendLıst);
                }

                if (person.getUserID().contains(reciveID)) {
                    message.reciveLıst.add("Mesaj ID : " + json.get("Mesaj ID : ").toString());
                    message.reciveLıst.add("Gönderen : " + json.get("Gönderen : ").toString());
                    message.reciveLıst.add("Alıcı : " + json.get("Alıcı : ").toString());
                    message.reciveLıst.add("Konu : " + json.get("Konu : ").toString());
                    reciveJSON = gson.toJson(message.reciveLıst);
                }
            }
            send = new File(userMessage, "Gönderilen.txt");
            receive = new File(userMessage, "Gelen.txt");
        }
        if (messageFile.exists()) {
            if (userMessage.exists()) {
                try (BufferedWriter bf = new BufferedWriter(new FileWriter(send))) {
                    send.mkdirs();
                    bf.write(sendJSON);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                try (BufferedWriter bfReciver = new BufferedWriter(new FileWriter(receive))) {
                    receive.mkdirs();
                    bfReciver.write(reciveJSON);
                }
                out.println("GELENLER ---> \n" + reciveJSON.toString());
                out.println("GÖNDERİLENLER ---> \n" + sendJSON.toString());
            } else {
                out.print("Mesaj Klasörü Bulunamadı ");
            }
        }
    }

    /**
     * Mesaj göndermek için kullanılan metotur
     *
     * @param tokens --> 3 parametre alır
     *               Birincisi send keywordü
     *               İkincisi mesajı göndereceğimiz kişi
     *               Üçüncü ise mesajın içeriği
     *
     * Mesaj gönderildiğinde her kullanıcı için Mesajlar klasörü altında username ile birlikte klasör oluşur
     * @throws IOException
     */
    private void handleSend(String[] tokens) throws IOException {
        if (tokens.length != 3) {
            out.println("ERROR: GEÇERLİ SEND FORMATINDA DEĞİL");
            return;
        }
        String sender = person.getAuthenticatedUser();
        String reciver = tokens[1];
        String[] topicAndContent = tokens[2].split(" ", 2);
        String subtopic = topicAndContent[0];
        String content = topicAndContent[1];
        message = new Message(
                sender,
                reciver,
                subtopic,
                content);

        System.out.println("authenticated User : " + sender);
        if (!person.getAuthenticatedUser().equals(sender)) {
            out.println("ERROR: KULLANICI GİRİŞ YAPMADI");
            return;
        }
        if (tokens.length > 3) {
            out.println("ERROR: GEÇERLİ MESSAGE FORMATINDA DEĞİL");
            return;
        }
        String user = readRegıster();
        if (user.contains(reciver)) {
            if (!messageFile.exists()) {
                messageFile.mkdirs();
            }
            try (BufferedWriter bfWriter = new BufferedWriter(new FileWriter(allMessage, true))) {
                if (allMessage.exists()) {
                    jsonObject.addProperty("Mesaj ID : ", message.messageId);
                    jsonObject.addProperty("Gönderen : ", message.sender);
                    jsonObject.addProperty("Alıcı : ", message.reciever);
                    jsonObject.addProperty("Konu : ", message.subtopic);
                    jsonObject.addProperty("İçerik : ", message.content);
                    gson.toJson(jsonObject, bfWriter);
                    bfWriter.newLine();
                    System.out.println("Dosyaya yazma işlemi başarılı.");
                } else {
                    allMessage.mkdirs();
                    jsonObject.addProperty("Mesaj ID: ", message.messageId);
                    jsonObject.addProperty("Gönderen: ", message.sender);
                    jsonObject.addProperty("Alıcı : ", message.reciever);
                    jsonObject.addProperty("Konu: ", message.subtopic);
                    jsonObject.addProperty("İçerik: ", message.content);
                    gson.toJson(jsonObject, bfWriter);
                    bfWriter.newLine();
                    System.out.println("Dosyaya yazma işlemi başarılı.");
                }
                userMessage = new File(messageFile, person.getAuthenticatedUser());
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.println("SEND SUCCESS");

        } else {
            out.println("ERROR: ALICI BULUNAMADI");
        }
    }


    /**
     * Kullanıcı giriş işlemlerinin yapıldığı metottur
     *
     * @param tokens --> 3 elemanlıdır
     *               İlki auth keywordü
     *               İkincisi username
     *               Üçüncüsü password
     * @throws IOException
     */
    private void handleAuth(String[] tokens) throws IOException {
        if (tokens.length != 3) {
            out.println("ERROR: GEÇERLİ AUTH FORMATINDA DEĞİL");
            return;
        }
        person.setUsername(tokens[1]);

        person.setPassword(tokens[2]);
        String content = "";
        System.out.println("UserCredantials: " + person.getUserCredentials());

        content = readRegıster();
        System.out.println("Content : ");
        if (content.contains(person.getUsername()) && content.contains(person.getPassword())) {
            person.setAuthenticatedUser(person.getUsername());
            person.setUserID();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(auth, true))) {
                if (auth.exists()) {
                    writer.write(person.getAuthenticatedUser());
                    writer.newLine();
                    System.out.println("Dosyaya yazma işlemi başarılı.");
                } else {
                    auth.mkdirs();
                    writer.write(person.getAuthenticatedUser());
                    writer.newLine();
                    System.out.println("Dosyaya yazma işlemi başarılı.");
                }
            } catch (IOException e) {
                System.out.println("Dosyaya yazarken bir hata oluştu: " + e.getMessage());
            }
            userMessage = new File(messageFile, person.getAuthenticatedUser());
            userMessage.mkdirs();
            out.println("AUTH SUCCESS");
        } else {
            out.println("AUTH FAILURE");
        }
    }

    /**
     * Kullanıcı kayıt işelmlerinin yapıldığı metot
     *
     * @param tokens --> tokens dizisi 3 eleman alır
     *               İlki regıster keywordü
     *               İkincisi username ---> unique bir degerdir. Kontrolü yapılır.
     *               Üçüncüsü password
     * @throws IOException
     */
    private void handleRegister(String[] tokens) throws IOException {
        if (tokens.length != 3) {
            out.println("ERROR: GEÇERLİ REGISTER FORMATINDA DEGİL");
            return;
        }
        String content = "";
        if (tokens[1].length() >= 3 && tokens[2].length() >= 6 && tokens[2].matches(".*\\d.*")) {
            person.setUsername(tokens[1]);
            person.setPassword(tokens[2]);
            person.setUserCredentials();

            System.out.println("UserCredentials: " + person.getUserCredentials());
            System.out.println("Username " + person.getUsername());
            System.out.println("Password : " + person.getPassword());
            if (register.exists() && Files.size(register.toPath()) != 0) {
                content = readRegıster();
                if (content.contains(person.getUsername())) {
                    out.println("DAHA ÖNCE KAYIT OLUNMUŞTUR !!");
                } else {
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(register))) {
                        bw.write(person.getUserCredentials().toString());
                        bw.newLine();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println("R: " + readRegıster());
                    out.println("REGISTER SUCCESS");
                }
            } else {
                messager.mkdirs();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(register, true))) {
                    bw.write(person.getUserCredentials().toString());
                    out.println("REGISTER SUCCESS");

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    out.println("REGISTER FAILURE");
                }

            }
        } else {
            out.println("KULLANICI ADI VEYA SİFRESİ UYGUN FORMATTA DEGİLDİR");
        }
    }

    /**
     * Kayıt.txt dosyasını String olarak okumaya yarayan metot
     */
    private String readRegıster() {
        StringBuilder stringBuilder = new StringBuilder("");
        String line = "";
        String content = "";
        try (BufferedReader bf = new BufferedReader(new FileReader(register))) {
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            content = stringBuilder.toString();
        } catch (IOException e) {
            System.out.println("Kayıt Dosyası Okunmadı !!");
            out.println("REGISTER FAILURE");
            System.out.println(e.getMessage());
        }
        return content;
    }

    /**
     * Mesajlar altındaki Tüm Mesajlar.txt içerisindeki verileri okuyup geriye JsonObject türünde veri göndermeye yarayan metot
     */
    private List<JsonObject> readMessage() {
        List<JsonObject> jsonObjectList = new ArrayList<>();
        StringBuilder jsonBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(allMessage))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("{") && line.endsWith("}")) {
                    try {
                        JsonObject jsonObject = JsonParser.parseString(line).getAsJsonObject();
                        jsonObjectList.add(jsonObject);
                    } catch (JsonSyntaxException e) {
                        System.err.println("Geçersiz JSON: " + line);
                        e.printStackTrace();
                    }
                } else {
                    jsonBuilder.append(line);
                    if (jsonBuilder.toString().endsWith("}")) {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(jsonBuilder.toString()).getAsJsonObject();
                            jsonObjectList.add(jsonObject);
                        } catch (JsonSyntaxException e) {
                            System.err.println("Geçersiz JSON: " + jsonBuilder);
                            e.printStackTrace();
                        }
                        jsonBuilder.setLength(0);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonObjectList;
    }

    /**
     * Kayıt.txt dosyasındaki userName ve Passwordleri okuyup onları map olarak tutmaya yarayan metot
     */
    private Map<String, String> setUsers() {
        List<String> userName = new ArrayList<>();
        List<String> password = new ArrayList<>();
        String userAndPasswordString = "";
        Map<String, String> userAndPasswordMap = new HashMap<>();
        String[] userCreadeantial = readRegıster().split(" ");
        for (String userPassword : userCreadeantial) {
            String[] users = userPassword.split(",");
            System.out.println("User: " + userPassword);
            for (String usersString : users) {
                String[] userAndPasswordSeperate = usersString.split("=");
                for (String userAndPasswordSeperateString : userAndPasswordSeperate) {
                    userAndPasswordString = userAndPasswordSeperateString.replace("{", "").replace(",", "").replace(",", "").replace("}", "").replace("[", "").replace("\n", "");
                    if (userAndPasswordString.matches(".*\\d.*")) {
                        password.add(userAndPasswordString);
                    } else {
                        userName.add(userAndPasswordString);
                    }
                }
            }

        }
        for (int i = 0; i < password.size(); i++) {
            userAndPasswordMap.put(userName.get(i), password.get(i));
        }
        return userAndPasswordMap;
    }
}




