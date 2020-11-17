package com.example.chatapplication.Model;

import com.google.firebase.database.Exclude;

import java.util.Date;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private Long date;

    public Chat() {
    }

    public Chat(String sender, String receiver, String message, Long date) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = date;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
