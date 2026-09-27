package com.gofar.gopay.infrastructure.persistence.payment;

import com.gofar.gopay.domain.payment.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long amount;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public PaymentEntity(Long amount, LocalDate date, PaymentStatus status) {
        this.amount = amount;
        this.date = date;
        this.status = status;
    }
}
