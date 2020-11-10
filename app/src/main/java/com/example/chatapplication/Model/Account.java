package com.example.chatapplication.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;


public class Account implements Serializable {
    private String uid;
    private String Username;
    private String Password;
    private String Email;

    public Account() {
    }

    public Account(String uid, String Username, String Password, String Email) {
        this.uid = uid;
        this.Username = Username;
        this.Password = Password;
        this.Email = Email;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("Username", Username);
        result.put("Email", Email);
        result.put("Password", Password);
        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}

