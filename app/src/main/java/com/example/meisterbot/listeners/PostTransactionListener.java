package com.example.meisterbot.listeners;

import com.example.meisterbot.models.TransactionApiResponse;

public interface PostTransactionListener {
    void didFetch(TransactionApiResponse response, String message);
    void didError(String message);
}
