package com.example.meisterbot.listeners;

import com.example.meisterbot.models.Payment;

public interface PaymentListener{
    void didFetch(Payment payment,String message);
    void didError(String message);
}
