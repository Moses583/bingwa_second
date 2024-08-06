package com.ujuzi.moses.listeners;

import com.ujuzi.moses.models.TariffApiResponse;

public interface GetTariffsListener {
    void didFetch(TariffApiResponse response, String message);
    void didError(String message);
}
