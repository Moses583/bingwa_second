package com.ujuzi.bingwasokonibot.listeners;

import com.ujuzi.bingwasokonibot.models.GetOffersResponse;

import java.util.List;

public interface GetOffersListener {
    void didFetch(List<GetOffersResponse> responses, String message);
    void didError(String message);
}
