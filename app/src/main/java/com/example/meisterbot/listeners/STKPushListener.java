package com.example.meisterbot.listeners;

import com.example.meisterbot.models.STKPushResponse;

public interface STKPushListener {
    void didFetch(STKPushResponse response, String message);
    void didError(String message);
}
