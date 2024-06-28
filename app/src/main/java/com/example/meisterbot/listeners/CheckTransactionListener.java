package com.example.meisterbot.listeners;

import com.example.meisterbot.models.CheckTransactionApiResponse;

public interface CheckTransactionListener {
    void didFetch(CheckTransactionApiResponse response, String message);
    void didError(String message);
}
