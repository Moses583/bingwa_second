package com.ujuzi.moses.models;

import java.io.Serializable;

public class OfferPOJO implements Serializable {
    private String offer,cost, ussd, dialSim,deviceId,subscriptionId,paymentSim,paymentSimId,offerTill;

    public OfferPOJO(String offer, String cost, String ussdCode, String dialSim, String deviceId, String subscriptionId, String paymentSim, String paymentSimId,String offerTill) {
        this.offer = offer;
        this.cost = cost;
        this.ussd = ussdCode;
        this.dialSim = dialSim;
        this.deviceId = deviceId;
        this.subscriptionId = subscriptionId;
        this.paymentSim = paymentSim;
        this.paymentSimId = paymentSimId;
        this.offerTill = offerTill;
    }

    public String getName(){
        return offer;
    }

    public String getAmount() {
        return cost;
    }

    public String getUssd() {
        return ussd;
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
