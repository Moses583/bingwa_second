package com.ujuzi.moses.listeners;

import com.ujuzi.moses.models.PostPersonaApiResponse;

public interface PostPersonaListener {
    void didFetch(PostPersonaApiResponse response, String message);
    void didError(String message);
}
