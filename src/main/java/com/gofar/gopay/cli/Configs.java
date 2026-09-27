package com.gofar.gopay.cli;

import com.gofar.gopay.cli.auth.AuthCommand;
import com.gofar.gopay.domain.customer.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.core.command.availability.Availability;
import org.springframework.shell.core.command.availability.AvailabilityProvider;
import org.springframework.shell.core.command.completion.CompletionProposal;
import org.springframework.shell.core.command.completion.CompletionProvider;
import org.springframework.shell.core.command.completion.CompositeCompletionProvider;
import org.springframework.shell.core.command.completion.EnumCompletionProvider;

import java.util.List;

@Configuration
public class Configs {

    private final AuthCommand authCommand;

    public Configs(AuthCommand authCommand) {
        this.authCommand = authCommand;
    }

    /**
     * Used to allow the execution of the login command.
     * Ok if the user is currently logged out, otherwise no
     *
     * @return the availability provider object
     */
    @Bean
    public AvailabilityProvider loginAvailabilityProvider() {
        return () -> authCommand.getCurrentUser() == null ? Availability.available() : Availability.unavailable("You are always logged in");
    }

    /**
     * Used to allow the execution of the logout command.
     * Ok if the user is currently logged in, otherwise no
     *
     * @return the availability provider object
     */
    @Bean
    public AvailabilityProvider logoutAvailabilityProvider() {
        return () -> authCommand.getCurrentUser() != null ? Availability.available() : Availability.unavailable("You are not logged in");
    }

    /**
     * The completion bean for the customer creation.
     * It provides the available status for the customer and completion for personal information
     * @return the completion provider object
     */
    @Bean
    public CompletionProvider createCustomerCompletionProvider() {
        EnumCompletionProvider enumCompletionProvider = new EnumCompletionProvider(Status.class, "--status");
        CompletionProvider completionProvider = completionContext -> List.of(
                new CompletionProposal("--name=Yves"),
                new CompletionProposal("--email=dupont.durant@gmail.com")
        );

        return new CompositeCompletionProvider(enumCompletionProvider, completionProvider);
    }

    /**
     * The completion bean for the customer list filtering
     * It display completion to filter customers by status
     * @return the completion provider object
     */
    @Bean
    public CompletionProvider customerFilterCompletionProvider() {
        return new EnumCompletionProvider(Status.class, "--status");
    }

}
