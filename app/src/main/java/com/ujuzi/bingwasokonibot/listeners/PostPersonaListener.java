package com.ujuzi.bingwasokonibot.listeners;

import com.ujuzi.bingwasokonibot.models.PostPersonaApiResponse;

public interface PostPersonaListener {
    void didFetch(PostPersonaApiResponse response, String message);
    void didError(String message);
}
