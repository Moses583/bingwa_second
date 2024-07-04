package com.bingwa.meisterbot.listeners;

import com.bingwa.meisterbot.models.GetOffersList;

public interface GetOffersListener {
    void didFetch(GetOffersList getOffersList, String message);
    void didError(String message);
}
