package com.example.meisterbot.listeners;

import com.example.meisterbot.models.TariffApiResponse;

public interface GetTariffsListener {
    void didFetch(TariffApiResponse response, String message);
    void didError(String message);
}
