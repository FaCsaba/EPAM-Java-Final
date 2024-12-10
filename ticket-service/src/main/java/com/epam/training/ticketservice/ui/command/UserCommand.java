package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.user.UserService;
import com.epam.training.ticketservice.core.user.persistence.User;
import org.springframework.shell.standard.ShellComponent;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class UserCommand {
    private final UserService userService;

    @ShellMethod(key = "sign up")
    public String signUp(String username, String password) {
        return userService.signUp(username, password)
                .toOptional()
                .map(Throwable::getMessage)
                .orElse(String.format("Successfully signed up with '%s'", username));
    }

    @ShellMethod(key = "sign in privileged")
    public String signInPrivileged(String username, String password) {
        return userService.signInPrivileged(username, password)
                .toOptional()
                .map(Throwable::getMessage)
                .orElse(String.format("Successfully signed in with '%s'", username));
    }

    @ShellMethod(key = "sign in")
    public String signInUnprivileged(String username, String password) {
        return userService.signInUnprivileged(username, password)
                .toOptional()
                .map(Throwable::getMessage)
                .orElse(String.format("Successfully signed in with '%s'", username));
    }


    @ShellMethod(key = "sign out")
    public String signOut() {
        return userService.signOut()
                .toOptional()
                .map(Throwable::getMessage)
                .orElse("Signed out");
    }

    @ShellMethod(key = "describe")
    public String describe() {
        return userService.getLoggedInUser().map(u -> {
            if (u.getRole() == User.Role.ADMIN) {
                return "Signed in with privileged account '" + u.getUsername() + "'";
            }
            return "Signed in with account '" + u.getUsername() + "'";
        }).orElse("You are not signed in");
    }
}
