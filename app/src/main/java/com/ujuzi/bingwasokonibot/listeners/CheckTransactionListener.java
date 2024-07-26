package com.ujuzi.bingwasokonibot.listeners;

import com.ujuzi.bingwasokonibot.models.CheckTransactionApiResponse;

public interface CheckTransactionListener {
    void didFetch(CheckTransactionApiResponse response, String message);
    void didError(String message);
}
