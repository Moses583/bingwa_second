package com.example.meisterbot.models;

public class ResetPasswordPojo {
    private String tillNumber,password;

    public ResetPasswordPojo(String tillNumber, String password) {
        this.tillNumber = tillNumber;
        this.password = password;
    }
}
