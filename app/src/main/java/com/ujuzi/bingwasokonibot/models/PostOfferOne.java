package com.ujuzi.bingwasokonibot.models;

import java.util.List;

public class PostOfferOne {
    private String tillNumber;
    private List<PostOfferTwo> offers;

    public PostOfferOne(String tillNumber, List<PostOfferTwo> offers) {
        this.tillNumber = tillNumber;
        this.offers = offers;
    }
}
