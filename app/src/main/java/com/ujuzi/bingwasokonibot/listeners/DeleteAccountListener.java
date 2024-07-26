package com.ujuzi.bingwasokonibot.listeners;

import com.ujuzi.bingwasokonibot.models.DeleteAccountApiResponse;

public interface DeleteAccountListener {
    void didFetch(DeleteAccountApiResponse response, String message);
    void didError(String message);
}
