package com.bingwa.bingwasokonibot.models;

public class RenewalPOJO {
    private String frequency,ussdCode,period,till,time;

    public RenewalPOJO(String frequency, String ussdCode, String period,String till,String time) {
        this.frequency = frequency;
        this.ussdCode = ussdCode;
        this.period = period;
        this.till = till;
        this.time = time;
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
    public String getTime() {
        return time;
    }
}
