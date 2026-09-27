package com.gofar.gopay.infrastructure.persistence.payment;

import com.gofar.gopay.domain.payment.Payment;
import com.gofar.gopay.domain.payment.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentRepositoryAdapter implements PaymentRepository {
    private final PaymentEntityRepository paymentEntityRepository;
    private final PaymentMapper paymentMapper;

    public PaymentRepositoryAdapter(PaymentEntityRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentEntityRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Optional<Payment> find(Long id) {
        return paymentEntityRepository.findById(id).map(paymentMapper::toPayment);
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity paymentEntity = paymentMapper.toEntity(payment);
        return paymentMapper.toPayment(paymentEntityRepository.save(paymentEntity));
    }
}
