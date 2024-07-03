package com.example.meisterbot.listeners;

import com.example.meisterbot.models.GetOffersList;
import com.example.meisterbot.models.GetOffersResponse;

import java.util.List;

public interface GetOffersListener {
    void didFetch(GetOffersList getOffersList, String message);
    void didError(String message);
}
