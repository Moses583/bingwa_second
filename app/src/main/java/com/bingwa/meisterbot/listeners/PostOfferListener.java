package com.bingwa.meisterbot.listeners;

import com.bingwa.meisterbot.models.PostOfferApiResponse;

public interface PostOfferListener {
    void didFetch(PostOfferApiResponse response,String message);
    void didError(String message);
}
