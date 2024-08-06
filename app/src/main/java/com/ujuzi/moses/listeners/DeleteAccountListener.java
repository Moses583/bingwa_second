package com.ujuzi.moses.listeners;

import com.ujuzi.moses.models.DeleteAccountApiResponse;

public interface DeleteAccountListener {
    void didFetch(DeleteAccountApiResponse response, String message);
    void didError(String message);
}
