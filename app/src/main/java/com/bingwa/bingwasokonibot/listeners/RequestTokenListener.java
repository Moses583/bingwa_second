package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.RequestTokenApiResponse;

public interface RequestTokenListener {
    void didFetch(RequestTokenApiResponse response, String message);
    void didError(String message);
}
