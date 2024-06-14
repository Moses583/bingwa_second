package com.example.meisterbot.models;

public class Persona {
     String name,storeName,bingwaSite,deviceId,phoneNumber,tillNumber,password;

    public Persona(String name, String storeName, String bingwaSite, String deviceId, String phoneNumber, String tillNumber,String password) {
        this.name = name;
        this.storeName = storeName;
        this.bingwaSite = bingwaSite;
        this.deviceId = deviceId;
        this.phoneNumber = phoneNumber;
        this.tillNumber = tillNumber;
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

    public String getBingwaSite() {
        return bingwaSite;
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
