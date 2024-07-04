package com.bingwa.meisterbot.listeners;

import com.bingwa.meisterbot.models.DeleteAccountApiResponse;

public interface DeleteAccountListener {
    void didFetch(DeleteAccountApiResponse response, String message);
    void didError(String message);
}
