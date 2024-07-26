package com.ujuzi.bingwasokonibot.listeners;

import com.ujuzi.bingwasokonibot.models.PostOfferApiResponse;

public interface PostOfferListener {
    void didFetch(PostOfferApiResponse response,String message);
    void didError(String message);
}
