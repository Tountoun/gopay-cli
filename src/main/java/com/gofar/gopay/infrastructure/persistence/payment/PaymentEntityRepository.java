package com.gofar.gopay.infrastructure.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentEntityRepository extends JpaRepository<PaymentEntity, Long> {
}
