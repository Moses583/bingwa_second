package com.ujuzi.bingwasokonibot.listeners;

import com.ujuzi.bingwasokonibot.models.ResetPasswordApiResponse;

public interface ResetPasswordListener {
    void didFetch(ResetPasswordApiResponse response, String message);
    void didError(String message);
}
