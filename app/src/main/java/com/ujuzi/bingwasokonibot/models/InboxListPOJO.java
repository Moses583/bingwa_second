package com.ujuzi.bingwasokonibot.models;

public class InboxListPOJO {
    private String message,timeStamp,id,sender,identifier,uniqueId,subId;

    public InboxListPOJO(String message, String timeStamp, String sender) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.sender = sender;
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
