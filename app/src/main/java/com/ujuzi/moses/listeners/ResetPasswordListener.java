package com.ujuzi.moses.listeners;

import com.ujuzi.moses.models.ResetPasswordApiResponse;

public interface ResetPasswordListener {
    void didFetch(ResetPasswordApiResponse response, String message);
    void didError(String message);
}
