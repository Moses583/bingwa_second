package com.bingwa.meisterbot.listeners;

import com.bingwa.meisterbot.models.STKPushResponse;

public interface STKPushListener {
    void didFetch(STKPushResponse response, String message);
    void didError(String message);
}
