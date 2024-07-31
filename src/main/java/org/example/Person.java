package org.example;

import java.util.*;

public class Person {
    private String username;
    private String password;

    private String authenticatedUser = "";

    private final Set<String> userID = new HashSet<>();

    private static Map<String, String> userCredentials = new HashMap<>();


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        try {
            if (username.length() >= 3) {
                this.username = username;
            }
        } catch (Exception e) {
            System.out.println("KULLANICI ADI UZUNLUĞU GEÇERLİ DEĞİL");
        }
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        try {
            if (password.length() >= 6) {
                this.password = password;
            }
        } catch (Exception e) {
            System.out.println("PARAOLA UZUNLUĞU GEÇERL DEĞİL");

        }
    }


    public Map<String, String> getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials() {
        try {
            System.out.println("UserCredanials Username: " + getUsername());
            System.out.println("UserCredanials Password: " + getPassword());
            if (!(userCredentials.containsKey((getUsername())) && userCredentials.containsValue(getPassword()))) {
                userCredentials.put(getUsername(), getPassword());
            }
        } catch (Exception e) {
            System.out.println("ERROR: KULLANICI DAHA ONCE KAYIT OLMUSTUR");
        }
    }

    public String getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(String authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

    }

    public void setUserID() {
        if (!getAuthenticatedUser().equals("")) {
            userID.add(username.substring(0, 3) + password.substring(0, 3));
        }
    }

    public Set<String> getUserID() {
        return this.userID;
    }

}


