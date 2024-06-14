package com.example.meisterbot.models;

public class TransactionPOJO {
    private String ussdResponse, transactionAmount, timeStamp, recipient;

    public TransactionPOJO(String ussdResponse, String transactionAmount, String timeStamp, String recipient) {
        this.ussdResponse = ussdResponse;
        this.transactionAmount = transactionAmount;
        this.timeStamp = timeStamp;
        this.recipient = recipient;
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
}
