package com.gofar.gopay.application;

import com.gofar.gopay.application.payment.PaymentService;
import com.gofar.gopay.domain.payment.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository  paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void assertRefundableWithInvalidIdShouldThrowPaymentNotFoundException() {
        doReturn(Optional.empty()).when(paymentRepository).find(1L);
        Exception ex = assertThrows(PaymentNotFoundException.class, () -> paymentService.assertRefundable(1L));

        Assertions.assertThat(ex.getMessage()).isNotNull();
        Assertions.assertThat(ex.getMessage()).contains("not found");
    }

    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = {"REFUNDED", "FAILED"})
    void assertRefundableForPaymentStatusNotSucceedShouldThrowInvalidPaymentStateException(
            PaymentStatus paymentStatus
    ) {
        final String CURRENT_STATUS_STRING  = "Current status: ";
        doReturn(Optional.of(getMockPayment(paymentStatus))).when(paymentRepository).find(1L);
        Exception ex = assertThrows(InvalidPaymentStateException.class, () -> paymentService.assertRefundable(1L));
        Assertions.assertThat(ex.getMessage()).isNotNull();
        Assertions.assertThat(ex.getMessage()).contains(CURRENT_STATUS_STRING +  paymentStatus.name());
    }

    @Test
    void assertRefundableForPaymentStatusSucceedShouldReturnReturnPayment() {
        Payment mockPayment = getMockPayment(PaymentStatus.SUCCESS);
        doReturn(Optional.of(mockPayment)).when(paymentRepository).find(1L);
        Payment refundCheckPayment = paymentService.assertRefundable(1L);
        Assertions.assertThat(refundCheckPayment).isEqualTo(mockPayment);
    }

    @Test
    void findWithValidIdShouldReturnOptionalPayment() {
        Payment mockPayment = getMockPayment(PaymentStatus.SUCCESS);
        doReturn(Optional.of(mockPayment)).when(paymentRepository).find(1L);
        Optional<Payment> refundCheckPayment = paymentService.find(1L);
        Assertions.assertThat(refundCheckPayment).isNotNull().isPresent();
        Assertions.assertThat(refundCheckPayment.get()).isNotNull();
        Assertions.assertThat(refundCheckPayment.get()).isEqualTo(mockPayment);
    }

    @Test
    void refundShouldSaveTheRefundedPayment() {
        Payment mockPayment = getMockPayment(PaymentStatus.SUCCESS);

        doReturn(Optional.of(mockPayment)).when(paymentRepository).find(1L);

        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        paymentService.refund(1L, "");
        verify(paymentRepository).save(paymentArgumentCaptor.capture());

        Assertions.assertThat(paymentArgumentCaptor.getValue()).isNotNull();
        Assertions.assertThat(paymentArgumentCaptor.getValue().id()).isEqualTo(mockPayment.id());
        Assertions.assertThat(paymentArgumentCaptor.getValue().amount()).isEqualTo(mockPayment.amount());
        Assertions.assertThat(paymentArgumentCaptor.getValue().date()).isEqualTo(mockPayment.date());
        Assertions.assertThat(paymentArgumentCaptor.getValue().status()).isEqualTo(PaymentStatus.REFUNDED);
    }


    private Payment getMockPayment(PaymentStatus status) {
        return new Payment(
                1L,
                500L,
                LocalDate.now(),
                status
        );
    }
}
