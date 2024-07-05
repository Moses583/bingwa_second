package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.TransactionApiResponse;

public interface PostTransactionListener {
    void didFetch(TransactionApiResponse response, String message);
    void didError(String message);
}
