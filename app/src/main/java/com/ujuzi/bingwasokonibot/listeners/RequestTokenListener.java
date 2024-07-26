package com.ujuzi.bingwasokonibot.listeners;

import com.ujuzi.bingwasokonibot.models.RequestTokenApiResponse;

public interface RequestTokenListener {
    void didFetch(RequestTokenApiResponse response, String message);
    void didError(String message);
}
