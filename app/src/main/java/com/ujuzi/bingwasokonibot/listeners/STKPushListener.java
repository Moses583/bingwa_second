package com.ujuzi.bingwasokonibot.listeners;

import com.ujuzi.bingwasokonibot.models.STKPushResponse;

public interface STKPushListener {
    void didFetch(STKPushResponse response, String message);
    void didError(String message);
}
