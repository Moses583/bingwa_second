package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.PostOfferApiResponse;

public interface PostOfferListener {
    void didFetch(PostOfferApiResponse response,String message);
    void didError(String message);
}
