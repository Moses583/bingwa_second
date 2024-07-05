package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.DeleteAccountApiResponse;

public interface DeleteAccountListener {
    void didFetch(DeleteAccountApiResponse response, String message);
    void didError(String message);
}
