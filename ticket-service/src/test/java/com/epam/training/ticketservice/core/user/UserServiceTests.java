package com.epam.training.ticketservice.core.user;

import com.epam.training.ticketservice.core.result.Result;
import com.epam.training.ticketservice.core.user.persistence.User;
import com.epam.training.ticketservice.core.user.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testUser", "password", User.Role.USER);
    }

    @Test
    void givenNewUser_whenSignUp_thenReturnUser() {
        when(userRepository.findById("testUser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        Result<User> result = userService.signUp("testUser", "password");

        assertTrue(result.isOk());
        assertEquals(user, result.unwrap());
    }

    @Test
    void givenAlreadyExistingUser_whenSignUp_thenError() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        Result<User> result = userService.signUp("testUser", "password");

        assertFalse(result.isOk());
        assertEquals("User already exists", result.unwrapErr().getMessage());
        verify(userRepository).findById("testUser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenUnprivilegedUser_whenSignInUnprivileged_thenSignIn() {
        when(userRepository.findByUsernameAndPassword("testUser", "password")).thenReturn(Optional.of(user));

        Result<User> result = userService.signInUnprivileged("testUser", "password");

        assertTrue(result.isOk());
        assertEquals(user, result.unwrap());
        assertEquals(Optional.of(user), userService.getLoggedInUser());
    }

    @Test
    void givenUnprivilegedUser_whenSignInPrivileged_thenError() {
        when(userRepository.findByUsernameAndPassword("testUser", "password")).thenReturn(Optional.of(user));

        Result<User> result = userService.signInPrivileged("testUser", "password");

        assertFalse(result.isOk());
        assertEquals("Login failed due to incorrect credentials", result.unwrapErr().getMessage());
    }

    @Test
    void givenAlreadySignedInUnprivilegedUser_whenSignInUnprivileged_thenError() {
        when(userRepository.findByUsernameAndPassword("testUser", "password")).thenReturn(Optional.of(user));
        userService.signInUnprivileged("testUser", "password");

        Result<User> result = userService.signInUnprivileged("testUser", "password");

        assertFalse(result.isOk());
        assertEquals("User already logged in", result.unwrapErr().getMessage());
    }

    @Test
    void givenAlreadySignedInUser_whenSignOut_thenSignOut() {
        when(userRepository.findByUsernameAndPassword("testUser", "password")).thenReturn(Optional.of(user));
        userService.signInUnprivileged("testUser", "password");

        Result<User> result = userService.signOut();

        assertTrue(result.isOk());
        assertEquals(Optional.empty(), userService.getLoggedInUser());
    }

    @Test
    void givenNotSignedInUser_whenSignOut_thenError() {
        Result<User> result = userService.signOut();

        assertFalse(result.isOk());
        assertEquals("No user to sign out", result.unwrapErr().getMessage());
    }

    @Test
    void givenPrivilegedUser_whenEnsurePrivileged_thenOk() {
        User adminUser = new User("adminUser", "password", User.Role.ADMIN);
        when(userRepository.findByUsernameAndPassword("adminUser", "password")).thenReturn(Optional.of(adminUser));
        userService.signInPrivileged("adminUser", "password");

        Result<User> result = userService.ensurePrivileged();

        assertTrue(result.isOk());
        assertEquals(adminUser, result.unwrap());
    }

    @Test
    void givenIncorrectUser_whenEnsurePrivileged_thenError() {
        userService.signInUnprivileged("testUser", "password"); // Log in a regular user first

        Result<User> result = userService.ensurePrivileged();

        assertFalse(result.isOk());
        assertEquals("Insufficient privilege", result.unwrapErr().getMessage());
    }

    @Test
    void givenUnprivilegedUser_whenEnsurePrivileged_thenError() {
        when(userRepository.findByUsernameAndPassword("testUser", "password")).thenReturn(Optional.of(user));
        userService.signInUnprivileged("testUser", "password"); // Log in a regular user first

        Result<User> result = userService.ensurePrivileged();

        assertFalse(result.isOk());
        assertEquals("Insufficient privilege", result.unwrapErr().getMessage());
    }
}
