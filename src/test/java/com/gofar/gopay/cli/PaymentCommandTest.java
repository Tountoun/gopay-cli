package com.gofar.gopay.cli;

import com.gofar.gopay.GopayOperationsCliApplication;
import com.gofar.gopay.cli.auth.AuthCommand;
import com.gofar.gopay.domain.customer.CustomerRepository;
import com.gofar.gopay.domain.payment.Payment;
import com.gofar.gopay.domain.payment.PaymentRepository;
import com.gofar.gopay.domain.payment.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellInputProvider;
import org.springframework.shell.test.ShellScreen;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.autoconfigure.ShellTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;

@ShellTest
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        useMainMethod = SpringBootTest.UseMainMethod.ALWAYS,
        classes = { GopayOperationsCliApplication.class },
        properties = { "spring.shell.interactive.enabled=false" }
)
@EnableAutoConfiguration(
        exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                FlywayAutoConfiguration.class
        }
)
class PaymentCommandTest {

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private PaymentRepository paymentRepository;

    @MockitoBean
    private AuthCommand authCommand;

    @BeforeEach
    void setUp() {
        doReturn("admin").when(authCommand).getCurrentUser();
    }


    @ParameterizedTest
    @EnumSource(value = PaymentStatus.class, names = {"FAILED", "REFUNDED"})
    void refundNotSuccessPaymentShouldShowErrorMessage(PaymentStatus paymentStatus, @Autowired ShellTestClient client) throws Exception {
        doReturn(Optional.of(getMockPayment(paymentStatus))).when(paymentRepository).find(1L);
        ShellInputProvider provider = ShellInputProvider.providerFor("payment refund -- 1")
                .withInput("Client complaint")
                .withInput("yes")
                .build();

        ShellScreen shellScreen = client.sendCommand(provider);

        ShellAssertions.assertThat(shellScreen).containsText("cannot be refunded");
        ShellAssertions.assertThat(shellScreen).containsText(paymentStatus.name());
    }

    @Test
    void refundNoExistingPaymentShouldShowErrorMessage(@Autowired ShellTestClient client) throws Exception {
        doReturn(Optional.empty()).when(paymentRepository).find(1L);
        ShellInputProvider provider = ShellInputProvider.providerFor("payment refund -- 1")
                .withInput("Client complaint")
                .withInput("yes")
                .build();

        ShellScreen shellScreen = client.sendCommand(provider);

        ShellAssertions.assertThat(shellScreen).containsText("not found");
    }

    @Test
    void executeRefundCommandShouldShowErrorMessage(@Autowired ShellTestClient client) throws Exception {
        doReturn(null).when(authCommand).getCurrentUser();
        ShellInputProvider provider = ShellInputProvider.providerFor("payment refund -- 1")
                .withInput("Client complaint")
                .withInput("yes")
                .build();

        ShellScreen shellScreen = client.sendCommand(provider);

        ShellAssertions.assertThat(shellScreen).containsText("Unauthenticated");
    }

    void cancelPaymentRefundShouldDisplayRefundCancellation(@Autowired ShellTestClient client) {
        // TODO: Today it's more complicated to test directly the componentFlow. If needed, a dumb terminal should be necessary
        // I created an issue for that on GitHub: https://github.com/spring-projects/spring-shell/issues/1383
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
