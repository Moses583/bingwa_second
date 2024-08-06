package com.ujuzi.moses.listeners;

import com.ujuzi.moses.models.Payment;

public interface PaymentListener{
    void didFetch(Payment payment,String message);
    void didError(String message);
}
