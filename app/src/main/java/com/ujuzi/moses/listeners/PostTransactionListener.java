package com.ujuzi.moses.listeners;

import com.ujuzi.moses.models.TransactionApiResponse;

public interface PostTransactionListener {
    void didFetch(TransactionApiResponse response, String message);
    void didError(String message);
}
