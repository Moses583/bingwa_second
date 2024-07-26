package com.ujuzi.bingwasokonibot.listeners;

import com.ujuzi.bingwasokonibot.models.TariffApiResponse;

public interface GetTariffsListener {
    void didFetch(TariffApiResponse response, String message);
    void didError(String message);
}
