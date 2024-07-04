package com.bingwa.meisterbot.listeners;

import com.bingwa.meisterbot.models.TransactionApiResponse;

public interface PostTransactionListener {
    void didFetch(TransactionApiResponse response, String message);
    void didError(String message);
}
