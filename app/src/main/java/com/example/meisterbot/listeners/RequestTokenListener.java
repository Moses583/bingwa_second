package com.example.meisterbot.listeners;

import com.example.meisterbot.models.RequestTokenApiResponse;

public interface RequestTokenListener {
    void didFetch(RequestTokenApiResponse response, String message);
    void didError(String message);
}
