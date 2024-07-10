package com.bingwa.bingwasokonibot.models;

public class RenewalPOJO {
    private String frequency,ussdCode,period,till,subId,dialSimCard;

    public RenewalPOJO(String frequency, String ussdCode, String period,String till,String subId, String dialSimCard) {
        this.frequency = frequency;
        this.ussdCode = ussdCode;
        this.period = period;
        this.till = till;
        this.subId = subId;
        this.dialSimCard = dialSimCard;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getUssdCode() {
        return ussdCode;
    }

    public String getPeriod() {
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
}
