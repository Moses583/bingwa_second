package com.ujuzi.bingwasokonibot.models;

public class DeleteAccountPojo {
    private String tillNumber, password;

    public DeleteAccountPojo(String tillNumber, String password) {
        this.tillNumber = tillNumber;
        this.password = password;
    }
}
