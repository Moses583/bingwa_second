package com.ujuzi.moses.listeners;

import com.ujuzi.moses.models.PostOfferApiResponse;

public interface PostOfferListener {
    void didFetch(PostOfferApiResponse response,String message);
    void didError(String message);
}
