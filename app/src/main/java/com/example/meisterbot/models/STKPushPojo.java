package com.example.meisterbot.models;

public class STKPushPojo {
    private String phone;
    private int amount;
    private String tillNumber;
    public STKPushPojo(String phone,int amount,String till) {
        this.phone = phone;
        this.amount = amount;
        this.tillNumber = till;
    }

    public String getPhone() {
        return phone;
    }
    public int getAmount() {
        return amount;
    }
    public String getTillNumber() {
        return tillNumber;
    }
}
