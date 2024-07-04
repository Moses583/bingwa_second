package com.bingwa.meisterbot.listeners;

import com.bingwa.meisterbot.models.PostPersonaApiResponse;

public interface PostPersonaListener {
    void didFetch(PostPersonaApiResponse response, String message);
    void didError(String message);
}
