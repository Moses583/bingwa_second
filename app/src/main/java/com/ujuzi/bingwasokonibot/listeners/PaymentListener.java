package com.ujuzi.bingwasokonibot.listeners;

import com.ujuzi.bingwasokonibot.models.Payment;

public interface PaymentListener{
    void didFetch(Payment payment,String message);
    void didError(String message);
}
