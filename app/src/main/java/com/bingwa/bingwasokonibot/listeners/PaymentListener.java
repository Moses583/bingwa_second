package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.Payment;

public interface PaymentListener{
    void didFetch(Payment payment,String message);
    void didError(String message);
}
