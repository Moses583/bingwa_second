package com.example.meisterbot.listeners;

import com.example.meisterbot.models.ResetPasswordApiResponse;

public interface ResetPasswordListener {
    void didFetch(ResetPasswordApiResponse response, String message);
    void didError(String message);
}
