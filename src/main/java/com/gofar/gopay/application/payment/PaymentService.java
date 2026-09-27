package com.gofar.gopay.application.payment;


import com.gofar.gopay.domain.payment.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Optional<Payment> find(Long id) {
        return paymentRepository.find(id);
    }

    public void refund(Long id, String reason) {
        Payment payment = assertRefundable(id);

        Payment refunded = new Payment(payment.id(), payment.amount(), payment.date(), PaymentStatus.REFUNDED);
        paymentRepository.save(refunded);
    }

    public Payment assertRefundable(Long id) {
        Payment payment = find(id).orElseThrow(() -> new PaymentNotFoundException(id));
        if (!PaymentStatus.SUCCESS.equals(payment.status())) {
            throw new InvalidPaymentStateException(id, payment.status());
        }
        return payment;
    }

}
