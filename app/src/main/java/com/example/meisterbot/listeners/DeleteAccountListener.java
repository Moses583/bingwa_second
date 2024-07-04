package com.example.meisterbot.listeners;

import com.example.meisterbot.models.CheckTransactionApiResponse;
import com.example.meisterbot.models.DeleteAccountApiResponse;

public interface DeleteAccountListener {
    void didFetch(DeleteAccountApiResponse response, String message);
    void didError(String message);
}
