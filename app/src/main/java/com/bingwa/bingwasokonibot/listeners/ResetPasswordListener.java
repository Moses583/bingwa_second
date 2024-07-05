package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.ResetPasswordApiResponse;

public interface ResetPasswordListener {
    void didFetch(ResetPasswordApiResponse response, String message);
    void didError(String message);
}
