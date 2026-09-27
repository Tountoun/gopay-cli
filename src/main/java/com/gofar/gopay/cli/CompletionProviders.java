package com.gofar.gopay.cli;


import com.gofar.gopay.domain.customer.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.core.command.completion.CompletionProposal;
import org.springframework.shell.core.command.completion.CompletionProvider;
import org.springframework.shell.core.command.completion.CompositeCompletionProvider;
import org.springframework.shell.core.command.completion.EnumCompletionProvider;

import java.util.Collections;
import java.util.List;

@Configuration
public class CompletionProviders {


    /**
     * The completion bean for the id argument of a command
     * @return the completion provider object
     */
    @Bean
    public CompletionProvider idCompletionProvider() {
        return completionContext -> Collections.singletonList(new CompletionProposal("--id=1"));
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
