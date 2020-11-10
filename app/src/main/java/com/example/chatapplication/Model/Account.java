package com.example.chatapplication.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;


public class Account implements Serializable {
    private String Image;
    private String CoverImage;
    private String Uid;
    private String Username;
    private String Email;
    private String Password;
    private String Address;
    private String Phone;

    public Account() {
    }

    public String getID() {
        return Uid;
    }

    public void setID(String ID) {
        this.Uid = ID;
    }

    public Account(String id, String username, String email, String password) {
        Uid = id;
        Username = username;
        Email = email;
        Password = password;
    }

    public Account(String image, String coverImage, String ID, String username, String email, String password, String address, String phone) {
        Image = image;
        CoverImage = coverImage;
        this.Uid = ID;
        Username = username;
        Email = email;
        Password = password;
        Address = address;
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getCoverImage() {
        return CoverImage;
    }

    public void setCoverImage(String coverImage) {
        CoverImage = coverImage;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Uid", Uid);
        result.put("Username", Username);
        result.put("Email", Email);
        result.put("Password", Password);
        result.put("Image", "");
        result.put("CoverImage", "");
        result.put("Address", "");
        result.put("Phone", "");
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

