package com.bingwa.meisterbot.models;

import java.io.Serializable;

public class OfferPOJO implements Serializable {
    private String name,amount,UssdCode, dialSim,deviceId,subscriptionId,paymentSim,paymentSimId,offerTill;

    public OfferPOJO(String name, String amount, String ussdCode, String dialSim, String deviceId, String subscriptionId, String paymentSim, String paymentSimId,String offerTill) {
        this.name = name;
        this.amount = amount;
        this.UssdCode = ussdCode;
        this.dialSim = dialSim;
        this.deviceId = deviceId;
        this.subscriptionId = subscriptionId;
        this.paymentSim = paymentSim;
        this.paymentSimId = paymentSimId;
        this.offerTill = offerTill;
    }

    public String getName(){
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getUssdCode() {
        return UssdCode;
    }

    public String getDialSim() {
        return dialSim;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getPaymentSim() {
        return paymentSim;
    }

    public String getPaymentSimId() {
        return paymentSimId;
    }

    public String getOfferTill() {
        return offerTill;
    }
}
