package com.gofar.gopay.cli;

import com.gofar.gopay.GopayOperationsCliApplication;
import com.gofar.gopay.domain.customer.Customer;
import com.gofar.gopay.domain.customer.CustomerRepository;
import com.gofar.gopay.domain.customer.Status;
import com.gofar.gopay.domain.payment.PaymentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.core.command.CommandNotFoundException;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellScreen;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.autoconfigure.ShellTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

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
class CustomerCommandTest {

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private PaymentRepository paymentRepository;

    @Test
    void listShouldDisplayCustomerTable(@Autowired ShellTestClient client) throws Exception {
        doReturn(List.of()).when(customerRepository).list();

        ShellScreen screen = client.sendCommand("customer list");

        ShellAssertions.assertThat(screen).containsText("id");
        ShellAssertions.assertThat(screen).containsText("name");
        ShellAssertions.assertThat(screen).containsText("email");
        ShellAssertions.assertThat(screen).containsText("status");
    }

    @Test
    void listShouldDisplayCustomerData(@Autowired ShellTestClient client) throws Exception {
        Customer customer = new Customer(2L, "Gopay", "gopay@gp.com", Status.ACTIVE);
        doReturn(List.of(customer)).when(customerRepository).list();
        ShellScreen screen = client.sendCommand("customer list");

        ShellAssertions.assertThat(screen)
                .containsText("2")
                .containsText("Gopay")
                .containsText("gopay@gp.com")
                .containsText("ACTIVE")
        ;
    }

    @Test
    void invalidCommandShouldThrowCommandNotFoundException(@Autowired ShellTestClient client) {
        Assertions.assertThatThrownBy(() -> client.sendCommand("customer lst"))
                .isInstanceOf(CommandNotFoundException.class);
    }


}
