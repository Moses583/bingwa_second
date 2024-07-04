package com.bingwa.meisterbot.models;

public class InboxListPOJO {
    private String message,timeStamp,id,sender;

    public InboxListPOJO(String message, String timeStamp, String sender) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.sender = sender;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }
    public String getTimeStamp() {
        return timeStamp;
    }
    public String getId(){
        return id;
    }
    public String getSender(){
        return sender;
    }
}
