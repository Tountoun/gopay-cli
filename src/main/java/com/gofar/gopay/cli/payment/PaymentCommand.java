package com.gofar.gopay.cli.payment;


import com.gofar.gopay.application.payment.PaymentService;
import com.gofar.gopay.cli.auth.AuthCommand;
import com.gofar.gopay.domain.payment.InvalidPaymentStateException;
import com.gofar.gopay.domain.payment.PaymentNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.jline.tui.component.flow.ComponentFlow;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PaymentCommand {

    private final ComponentFlow.Builder builder;
    private final PaymentService paymentService;
    private final AuthCommand authCommand;
    private static final Logger AUDIT = LoggerFactory.getLogger("AUDIT");

    public PaymentCommand(ComponentFlow.Builder builder, PaymentService paymentService, AuthCommand authCommand) {
        this.builder = builder;
        this.paymentService = paymentService;
        this.authCommand = authCommand;
    }


    @Command(name = {"payment", "refund"}, description = "Refund a customer")
    public String refund(
            @Argument(index = 0, description = "The customer to refund id")
            @NotNull(message = "The customer to refund's id is required") Long id
    ) {
        if (Objects.isNull(authCommand.getCurrentUser())) {
            return colored("Unauthenticated! You need to login first", AttributedStyle.RED);
        }
        try {
            paymentService.assertRefundable(id);
        } catch (PaymentNotFoundException | InvalidPaymentStateException e) {
            return colored(e.getMessage(), AttributedStyle.RED);
        }

        ComponentFlow flow = builder.clone()
                .reset()
                .withStringInput("reason")
                .name("Reason:")
                .and()
                .withConfirmationInput("confirm")
                .name("Confirm refund ?").and().build();

        ComponentFlow.ComponentFlowResult flowResult = flow.run();
        boolean confirmed = flowResult.getContext().get("confirm");
        String reason = flowResult.getContext().get("reason");

        if (!confirmed) {
            AttributedStringBuilder sb = new AttributedStringBuilder();
            sb.append("The refund was cancelled");
            return sb.toAnsi();
        }
        try {
            paymentService.refund(id, reason);
            AUDIT.info("User {} refunded payment with id {}. Reason: {}", authCommand.getCurrentUser(), id, reason);
            return colored("Refund successful", AttributedStyle.GREEN);
        } catch (PaymentNotFoundException | InvalidPaymentStateException e) {
            return colored(e.getMessage(), AttributedStyle.RED);
        }
    }

    /**
     * Set the coloring of a message
     * @param message the message to color
     * @param color the corresponding int value of the color
     * @return the message colored
     */
    private static String colored(String message, int color) {
        AttributedStringBuilder sb = new AttributedStringBuilder();
        sb.style(AttributedStyle.DEFAULT.foreground(color));
        sb.append(message);
        return sb.toAnsi();
    }
}
