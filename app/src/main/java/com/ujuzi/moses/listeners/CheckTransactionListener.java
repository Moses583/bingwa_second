package com.ujuzi.moses.listeners;

import com.ujuzi.moses.models.CheckTransactionApiResponse;

public interface CheckTransactionListener {
    void didFetch(CheckTransactionApiResponse response, String message);
    void didError(String message);
}
