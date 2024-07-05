package com.bingwa.bingwasokonibot.models;

public class TransactionPOJO {
    private String ussdResponse, transactionAmount, timeStamp, recipient, status,ussd,messageFull;
    private int subId,till;

    public TransactionPOJO(String ussdResponse, String transactionAmount, String timeStamp, String recipient, String status, int subId,String ussd, int till, String messageFull) {
        this.ussdResponse = ussdResponse;
        this.transactionAmount = transactionAmount;
        this.timeStamp = timeStamp;
        this.recipient = recipient;
        this.status = status;
        this.ussd = ussd;
        this.subId = subId;
        this.till = till;
        this.messageFull = messageFull;
    }

    public String getUssdResponse() {
        return ussdResponse;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getStatus() {
        return status;
    }

    public String getUssd() {
        return ussd;
    }

    public String getMessageFull() {
        return messageFull;
    }

    public int getSubId() {
        return subId;
    }

    public int getTill() {
        return till;
    }
}
