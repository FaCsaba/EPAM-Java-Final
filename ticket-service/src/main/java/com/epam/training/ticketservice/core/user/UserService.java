package com.epam.training.ticketservice.core.user;

import java.util.Optional;

import com.epam.training.ticketservice.core.result.Result;
import com.epam.training.ticketservice.core.user.persistence.User;

public interface UserService {
    Result<User> signUp(String username, String password);

    Result<User> signOut();

    Result<User> signInPrivileged(String username, String password);

    Result<User> signInUnprivileged(String username, String password);

    Optional<User> getLoggedInUser();

    Result<User> ensurePrivileged();
}
