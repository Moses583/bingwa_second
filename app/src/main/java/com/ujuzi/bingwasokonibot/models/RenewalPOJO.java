package com.ujuzi.bingwasokonibot.models;

public class RenewalPOJO {
    private String frequency,ussdCode,till,subId,dialSimCard, money,dateCreation,dateExpiry;
    private int period;

    public RenewalPOJO(String frequency, String ussdCode, int period,String till,String subId, String dialSimCard, String money, String dateCreation, String dateExpiry) {
        this.frequency = frequency;
        this.ussdCode = ussdCode;
        this.period = period;
        this.till = till;
        this.subId = subId;
        this.dialSimCard = dialSimCard;
        this.money = money;
        this.dateCreation = dateCreation;
        this.dateExpiry = dateExpiry;

    }

    public String getFrequency() {
        return frequency;
    }
    public String getUssdCode() {
        return ussdCode;
    }
    public int getPeriod() {
        return period;
    }
    public String getTill() {
        return till;
    }
    public String getSubId() {
        return subId;
    }
    public String getDialSimCard() {
        return dialSimCard;
    }
    public String getMoney() {
        return money;
    }
    public String getDateCreation() {
        return dateCreation;
    }
    public String getDateExpiry() {
        return dateExpiry;
    }
}
