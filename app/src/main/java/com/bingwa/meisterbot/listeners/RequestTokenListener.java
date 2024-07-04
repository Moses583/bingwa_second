package com.bingwa.meisterbot.listeners;

import com.bingwa.meisterbot.models.RequestTokenApiResponse;

public interface RequestTokenListener {
    void didFetch(RequestTokenApiResponse response, String message);
    void didError(String message);
}
