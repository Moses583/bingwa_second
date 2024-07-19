package com.bingwa.bingwasokonibot.models;

public class PostOfferTwo {
    private String offer, cost, ussd, dialSim,deviceId,subscriptionId,paymentSim,paymentSimId,offerTill;

    public PostOfferTwo(String offer, String cost, String ussd, String dialSim, String deviceId, String subscriptionId, String paymentSim, String paymentSimId,String offerTill) {
        this.offer = offer;
        this.cost = cost;
        this.ussd = ussd;
        this.dialSim = dialSim;
        this.deviceId = deviceId;
        this.subscriptionId = subscriptionId;
        this.paymentSim = paymentSim;
        this.paymentSimId = paymentSimId;
        this.offerTill = offerTill;
    }
}
