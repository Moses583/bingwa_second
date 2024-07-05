package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.GetOffersList;

public interface GetOffersListener {
    void didFetch(GetOffersList getOffersList, String message);
    void didError(String message);
}
