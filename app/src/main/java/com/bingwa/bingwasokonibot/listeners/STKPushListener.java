package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.STKPushResponse;

public interface STKPushListener {
    void didFetch(STKPushResponse response, String message);
    void didError(String message);
}
