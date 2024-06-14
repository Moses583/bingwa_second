package com.example.meisterbot.listeners;

import com.example.meisterbot.models.PostOfferApiResponse;

public interface PostOfferListener {
    void didFetch(PostOfferApiResponse response,String message);
    void didError(String message);
}
