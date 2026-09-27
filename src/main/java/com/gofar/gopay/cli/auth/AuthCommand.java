package com.gofar.gopay.cli.auth;

import lombok.Getter;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.core.command.CommandContext;
import org.springframework.shell.core.command.annotation.Argument;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Getter
@Component
public class AuthCommand {

    private String currentUser;


    /**
     * The login command for the user. Example: login -- username
     * The password read if simply compared to admin.
     *
     * @param username the username of the user
     * @param context the command context object for user input
     * @return a text corresponding to the login result
     * @throws Exception for reading password from shell
     */
    @Command(name = "login", description = "Login with username and password",
            help = "login -- username", availabilityProvider = "loginAvailabilityProvider")
    public String login(@Argument(index = 0, description = "The username") String username, CommandContext context) throws Exception {
        char[] password = context.inputReader().readPassword("Password:");
        if ("admin".equals(username) && Arrays.equals(password, "admin".toCharArray())) {
            AttributedStringBuilder stringBuilder = new AttributedStringBuilder();
            AttributedStyle style = AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN);
            stringBuilder.style(style);
            stringBuilder.append("Welcome! Log in as admin successfully");
            this.currentUser = username;
            Arrays.fill(password, '*');
            return stringBuilder.toAnsi();
        }
        AttributedStringBuilder stringBuilder = new AttributedStringBuilder();
        AttributedStyle style = AttributedStyle.DEFAULT.foreground(AttributedStyle.RED);
        stringBuilder.style(style);
        stringBuilder.append("Error! Invalid username or password");
        return stringBuilder.toAnsi();
    }

    @Command(name = "logout", description = "Logout", help = "logout", availabilityProvider = "logoutAvailabilityProvider")
    public String logout() {
        this.currentUser = null;
        AttributedStringBuilder stringBuilder = new AttributedStringBuilder();
        AttributedStyle style = AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN);
        stringBuilder.style(style);
        stringBuilder.append("Logout successfully");
        return stringBuilder.toAnsi();
    }


}
