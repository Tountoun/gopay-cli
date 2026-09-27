package com.gofar.gopay.domain.payment;

import java.time.LocalDate;

public record Payment(Long id, Long amount, LocalDate date, PaymentStatus status) {
}
