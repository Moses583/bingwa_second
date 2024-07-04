package com.bingwa.meisterbot.listeners;

import com.bingwa.meisterbot.models.TariffApiResponse;

public interface GetTariffsListener {
    void didFetch(TariffApiResponse response, String message);
    void didError(String message);
}
