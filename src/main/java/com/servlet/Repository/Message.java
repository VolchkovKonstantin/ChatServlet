package com.servlet.Repository;

import java.util.Date;

/**
 * Created by Xaker on 28.04.2015.
 */
public class Message {
    private long id;
    private String user;
    private String message;
    private boolean flag;
    private Date date;

    public Message(long id, String user, String message, Date date) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.flag = true;
        this.date = date;
    }
    public Message() {
        this.id = 0;
        this.user = "Petya";
        this.message = "Hello";
        this.flag = true;
    }
    public long getID() {
        return this.id;
    }

    public String getUser() {
        return this.user;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean getFlag() {
        return this.flag;
    }

    public void setID(long id) {
        this.id = id;
        return;
    }

    public void setUser(String username) {
        this.user = username;
        return;
    }

    public void setMessage(String message) {
        this.message = message;
        return;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Message getStructofMessage(long id, String user, String message, Date date) {
        Message structMessage = new Message(id, user, message, date);
        return structMessage;
    }

    public String toString() {
        return "{\"id\":\"" + this.id + "\",\"user\":\"" + this.user + "\",\"message\":" + this.message + "}";
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
