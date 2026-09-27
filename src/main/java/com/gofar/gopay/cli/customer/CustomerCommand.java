package com.gofar.gopay.cli.customer;

import com.gofar.gopay.domain.customer.Status;
import com.gofar.gopay.application.customer.CustomerService;
import com.gofar.gopay.domain.customer.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.shell.jline.tui.table.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class CustomerCommand {

    private static final String ACTIVE = "ACTIVE";
    private final CustomerService customerService;

    public CustomerCommand(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Command(name = {"customer", "find"}, description = "Get a customer by id", help = "customer find -- id", completionProvider = "idCompletionProvider")
    public String find(
            @Argument(index = 0, description = "The ID of the customer") @NotNull(message = "The customer id is required") Long id
    ) {
        Optional<Customer> optionalCustomer = customerService.find(id);
        if (optionalCustomer.isPresent()) {
            return String.format("Customer #%d: %s (%s)", id, optionalCustomer.get().name(), optionalCustomer.get().status().name());
        }
        AttributedStringBuilder sb = new AttributedStringBuilder();
        sb.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        sb.append(String.format("Customer not found: %d", id));
        return sb.toAnsi();
    }

    @Command(name = {"customer", "create"}, description = "Create a new customer", help = "customer create --name Yves --email yves.tiger@gmail.com --status ACTIVE", completionProvider = "createCustomerCompletionProvider")
    public String create(
            @Option(longName = "name") @NotBlank(message = "The customer name is required") String name,
            @Option(longName = "email") @NotBlank(message = "The customer email is required") @Email(message = "The customer email is not valid") String email,
            @Option(longName = "status", defaultValue = ACTIVE) Status status
    ) {
        customerService.create(name, email, status);
        AttributedStringBuilder sb = new AttributedStringBuilder();
        sb.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
        sb.append(String.format("Customer created: %s <%s> [%s] ✓", name, email, status.name()));
        return sb.toAnsi();
    }


    @Command(name = {"customer", "list"}, description = "Get all customers or filter by status", help = "customer list [--status=ACTIVE|INACTIVE]", completionProvider = "customerFilterCompletionProvider")
    public String list(@Option(longName = "status") Status status) {
        List<Customer> customers;
        if (Objects.isNull(status)) {
            customers = customerService.list();
        } else {
            customers = customerService.byStatus(status);
        }
        String[][] customerArray = new String[customers.size() + 1][];
        customerArray[0] = new String[]{"id", "name", "email", "status"};

        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            customerArray[i+1] = new String[]{
                    String.valueOf(customer.id()), customer.name(), customer.email(), customer.status().name()
            };
        }
        TableModel model = new ArrayTableModel(customerArray);
        TableBuilder builder = new TableBuilder(model);
        builder.addFullBorder(BorderStyle.fancy_light);
        return builder.build().render(100);
    }

}
