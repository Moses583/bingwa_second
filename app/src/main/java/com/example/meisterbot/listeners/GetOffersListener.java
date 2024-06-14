package com.example.meisterbot.listeners;

import com.example.meisterbot.models.GetOfferApiResponse;
import com.example.meisterbot.models.OfferListResponse;

import java.util.List;

public interface GetOffersListener {
    void didFetch(List<GetOfferApiResponse> responseList, String message);
    void didError(String message);
}
