package com.bingwa.meisterbot.listeners;

import com.bingwa.meisterbot.models.ResetPasswordApiResponse;

public interface ResetPasswordListener {
    void didFetch(ResetPasswordApiResponse response, String message);
    void didError(String message);
}
