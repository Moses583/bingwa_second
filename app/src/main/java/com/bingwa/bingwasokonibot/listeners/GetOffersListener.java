package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.GetOffersResponse;

import java.util.List;

public interface GetOffersListener {
    void didFetch(List<GetOffersResponse> responses, String message);
    void didError(String message);
}
