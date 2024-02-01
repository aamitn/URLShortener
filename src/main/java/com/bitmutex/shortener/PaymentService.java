package com.bitmutex.shortener;

public interface PaymentService {
    boolean verifyPayment(RazorpayPaymentDetails paymentDetails);
}