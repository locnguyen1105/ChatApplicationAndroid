package com.example.chatapplication.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;


public class Account implements Serializable {
    public String ID;
    public String Username;
    public String Email;
    public String Password;

    public Account() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Account(String id, String username, String email, String password) {
        ID = id;
        Username = username;
        Email = email;
        Password = password;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", ID);
        result.put("Username", Username);
        result.put("Email", Email);
        result.put("Password", Password);
        return result;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}

