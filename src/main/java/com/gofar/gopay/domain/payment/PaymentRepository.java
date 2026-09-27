package com.gofar.gopay.domain.payment;

import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> find(Long id);
    Payment save(Payment payment);
}
