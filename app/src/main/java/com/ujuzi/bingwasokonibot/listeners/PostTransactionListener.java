package com.ujuzi.bingwasokonibot.listeners;

import com.ujuzi.bingwasokonibot.models.TransactionApiResponse;

public interface PostTransactionListener {
    void didFetch(TransactionApiResponse response, String message);
    void didError(String message);
}
