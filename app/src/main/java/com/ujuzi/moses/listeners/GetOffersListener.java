package com.ujuzi.moses.listeners;

import com.ujuzi.moses.models.GetOffersResponse;

import java.util.List;

public interface GetOffersListener {
    void didFetch(List<GetOffersResponse> responses, String message);
    void didError(String message);
}
