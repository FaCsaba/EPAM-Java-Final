package com.epam.training.ticketservice.core.user;

import com.epam.training.ticketservice.core.result.Result;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.persistence.UserRepository;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private Optional<User> loggedInUser = Optional.empty();

    @Override
    public Result<User> signUp(String username, String password) {
        var userMaybe = userRepository.findById(username);
        if (userMaybe.isPresent()) {
            return Result.err(new Error("User already exists"));
        }
        return Result.ok(userRepository.save(new User(username, password, User.Role.USER)));
    }

    @Override
    public Result<User> signOut() {
        return Result.fromOptional(loggedInUser, new Error("No user to sign out"))
                .use(u -> loggedInUser = Optional.empty());
    }

    @Override
    public Result<User> signInPrivileged(String username, String password) {
        return signIn(username, password, User.Role.ADMIN);
    }

    @Override
    public Result<User> signInUnprivileged(String username, String password) {
        return signIn(username, password, User.Role.USER);
    }

    private Result<User> signIn(String username, String password, User.Role privilege) {
        return Result.fromOptional(
                        userRepository.findByUsernameAndPassword(username, password),
                        new Error("Login failed due to incorrect credentials"))
                .flatMap(u -> {
                    if (loggedInUser.isPresent()) {
                        return Result.err(new Error("User already logged in"));
                    }
                    if (u.getRole() != privilege) {
                        return Result.err(new Error("Login failed due to incorrect credentials"));
                    }
                    return Result.ok(u);
                }).use(u -> loggedInUser = Optional.of(u));
    }

    @Override
    public Optional<User> getLoggedInUser() {
        return loggedInUser;
    }

    @Override
    public Result<User> ensurePrivileged() {
        var err = new Error("Insufficient privilege");
        return Result.fromOptional(loggedInUser, err).flatMap(u -> {
            if (u.getRole() == User.Role.ADMIN) {
                return Result.ok(u);
            }
            return Result.err(err);
        });
    }
}
