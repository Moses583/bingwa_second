package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.TariffApiResponse;

public interface GetTariffsListener {
    void didFetch(TariffApiResponse response, String message);
    void didError(String message);
}
