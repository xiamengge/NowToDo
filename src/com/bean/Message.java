package com.bean;

public class Message {
    private String send;

    private String recv;

    private String msg;
    public Message(){
    	super();
    }
    public Message(String send,String recv,String msg){
    	super();
    	this.send=send;
    	this.recv=recv;
    	this.msg=msg;
    	
    }
   
    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send == null ? null : send.trim();
    }

    public String getRecv() {
        return recv;
    }

    public void setRecv(String recv) {
        this.recv = recv == null ? null : recv.trim();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg == null ? null : msg.trim();
    }
}