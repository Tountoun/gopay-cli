package com.gofar.gopay.cli;


import com.gofar.gopay.GopayOperationsCliApplication;
import com.gofar.gopay.cli.auth.AuthCommand;
import com.gofar.gopay.domain.customer.CustomerRepository;
import com.gofar.gopay.domain.payment.PaymentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
class AuthCommandTest {

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private PaymentRepository paymentRepository;

    @Autowired
    private AuthCommand authCommand;

    @Test
    void loginWithValidCredentials(@Autowired ShellTestClient client) throws Exception {
        authCommand.logout();

        ShellScreen shellScreen = login(client, "admin", "admin");
        ShellAssertions.assertThat(shellScreen).containsText("Welcome");
        Assertions.assertEquals("admin", authCommand.getCurrentUser());
    }

    @Test
    void loginWithInvalidCredentials(@Autowired ShellTestClient client) throws Exception {
        authCommand.logout();

        ShellScreen shellScreen = login(client, "admin", "invalid");
        ShellAssertions.assertThat(shellScreen).containsText("Error");
        Assertions.assertNull(authCommand.getCurrentUser());

        shellScreen = login(client, "invalid", "admin");
        ShellAssertions.assertThat(shellScreen).containsText("Error");
        Assertions.assertNull(authCommand.getCurrentUser());
    }

    @Test
    void loginAvailabilityTest(@Autowired ShellTestClient client) throws Exception {
        authCommand.logout();
        login(client, "admin", "admin");
        ShellScreen shellScreen = login(client, "admin", "admin");
        ShellAssertions.assertThat(shellScreen).containsText("already logged in");
    }

    @Test
    void logoutAvailabilityTest(@Autowired ShellTestClient client) throws Exception {
        authCommand.logout();
        login(client, "admin", "admin");
        authCommand.logout();

        ShellInputProvider inputProvider = ShellInputProvider.providerFor("logout")
                .build();

        ShellScreen shellScreen = client.sendCommand(inputProvider);
        ShellAssertions.assertThat(shellScreen).containsText("not logged in");
    }


    private ShellScreen login(ShellTestClient client, String username, String password) throws Exception {
        ShellInputProvider inputProvider = ShellInputProvider.providerFor("login -- " + username)
                .withPassword(password)
                .build();

        return client.sendCommand(inputProvider);
    }
}
