package com.ujuzi.moses.listeners;

import com.ujuzi.moses.models.RequestTokenApiResponse;

public interface RequestTokenListener {
    void didFetch(RequestTokenApiResponse response, String message);
    void didError(String message);
}
