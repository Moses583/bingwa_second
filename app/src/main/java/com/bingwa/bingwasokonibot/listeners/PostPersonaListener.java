package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.PostPersonaApiResponse;

public interface PostPersonaListener {
    void didFetch(PostPersonaApiResponse response, String message);
    void didError(String message);
}
