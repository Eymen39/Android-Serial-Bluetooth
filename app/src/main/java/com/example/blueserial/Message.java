package com.example.blueserial;

public class Message {

    private String msg;
    private String zeitstempel;
    boolean sent;

    public Message(String msg,String zeits,boolean sent){
        this.msg=msg;
        this.zeitstempel=zeits;
        this.sent=sent;

    }

    public String getMsg(){
        return msg;
    }
    public String getZeitstempel(){
        return zeitstempel;
    }
}
