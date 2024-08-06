package com.ujuzi.moses.models;

public class Transaction {
    private String message, phone;
    private double amount;
    private int tillNumber;

    public Transaction(String message, String phone, double amount, int tillNumber) {
        this.message = message;
        this.phone = phone;
        this.amount = amount;
        this.tillNumber = tillNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getPhone() {
        return phone;
    }

    public double getAmount() {
        return amount;
    }

    public int getTillNumber() {
        return tillNumber;
    }
}
