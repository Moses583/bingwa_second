package com.example.meisterbot.models;

public class Persona {
     String name,storeName,deviceId,phoneNumber,tillNumber,password;

    public Persona(String name, String phoneNumber, String tillNumber, String storeName, String deviceId,String password) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.tillNumber = tillNumber;
        this.storeName = storeName;
        this.deviceId = deviceId;
        this.password = password;
    }

    public String getPassWord() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTillNumber() {
        return tillNumber;
    }
}
