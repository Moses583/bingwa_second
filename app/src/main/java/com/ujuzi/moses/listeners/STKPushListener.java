package com.ujuzi.moses.listeners;

import com.ujuzi.moses.models.STKPushResponse;

public interface STKPushListener {
    void didFetch(STKPushResponse response, String message);
    void didError(String message);
}
