package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.CheckTransactionApiResponse;

public interface CheckTransactionListener {
    void didFetch(CheckTransactionApiResponse response, String message);
    void didError(String message);
}
