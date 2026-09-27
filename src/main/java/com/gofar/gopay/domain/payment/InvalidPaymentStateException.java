package com.gofar.gopay.domain.payment;

public class InvalidPaymentStateException extends RuntimeException {

    public InvalidPaymentStateException(Long paymentId, PaymentStatus currentStatus) {
        super("Payment " + paymentId + " cannot be refunded. Current status: " + currentStatus);
    }
}
