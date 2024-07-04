package com.bingwa.meisterbot.listeners;

import com.bingwa.meisterbot.models.CheckTransactionApiResponse;

public interface CheckTransactionListener {
    void didFetch(CheckTransactionApiResponse response, String message);
    void didError(String message);
}
