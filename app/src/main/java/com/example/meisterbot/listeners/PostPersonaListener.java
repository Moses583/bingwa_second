package com.example.meisterbot.listeners;

import com.example.meisterbot.models.PostPersonaApiResponse;

public interface PostPersonaListener {
    void didFetch(PostPersonaApiResponse response, String message);
    void didError(String message);
}
