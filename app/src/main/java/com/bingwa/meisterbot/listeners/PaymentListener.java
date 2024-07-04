package com.bingwa.meisterbot.listeners;

import com.bingwa.meisterbot.models.Payment;

public interface PaymentListener{
    void didFetch(Payment payment,String message);
    void didError(String message);
}
