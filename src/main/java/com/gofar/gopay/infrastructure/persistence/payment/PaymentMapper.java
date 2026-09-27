package com.gofar.gopay.infrastructure.persistence.payment;

import com.gofar.gopay.domain.payment.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    Payment toPayment(PaymentEntity paymentEntity);

    PaymentEntity toEntity(Payment payment);
}
