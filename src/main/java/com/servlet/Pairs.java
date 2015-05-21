package com.servlet;

/**
 * Created by Xaker on 19.05.2015.
 */
public class Pairs {
    private Message message;
    private String request;

    public Pairs()
    {
        message = new Message();
        request = "";
    }

    public Pairs(String request, Message message)
    {
        this.message = message;
        this.request = request;
    }

    public Message getMessage() {
        return message;
    }

    public String getRequest() {
        return request;
    }

}
