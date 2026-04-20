package edu.escuelaing.arep.monolith.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.escuelaing.arep.monolith.entity.User;
import edu.escuelaing.arep.monolith.exception.ApiException;
import edu.escuelaing.arep.monolith.exception.ErrorCode;
import edu.escuelaing.arep.monolith.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getByIdShouldReturnUserWhenExists() {
        User expected = User.builder().id("u1").build();
        when(userRepository.findById("u1")).thenReturn(Optional.of(expected));

        User actual = userService.getById("u1");

        assertSame(expected, actual);
    }

    @Test
    void getByIdShouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> userService.getById("missing"));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getByAuth0IdShouldReturnUserWhenExists() {
        User expected = User.builder().id("u1").auth0Id("auth0|1").build();
        when(userRepository.findByAuth0Id("auth0|1")).thenReturn(Optional.of(expected));

        User actual = userService.getByAuth0Id("auth0|1");

        assertSame(expected, actual);
    }

    @Test
    void getByAuth0IdShouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByAuth0Id("auth0|missing")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> userService.getByAuth0Id("auth0|missing"));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createUserShouldReturnExistingUserWhenAuth0IdAlreadyExists() {
        User existing = User.builder().id("u1").auth0Id("auth0|1").email("a@test.com").build();
        when(userRepository.existsByAuth0Id("auth0|1")).thenReturn(true);
        when(userRepository.findByAuth0Id("auth0|1")).thenReturn(Optional.of(existing));

        User actual = userService.createUser("auth0|1", "a@test.com");

        assertSame(existing, actual);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserShouldThrowWhenEmailAlreadyExistsForAnotherUser() {
        when(userRepository.existsByAuth0Id("auth0|new")).thenReturn(false);
        when(userRepository.existsByEmail("used@test.com")).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.createUser("auth0|new", "used@test.com"));

        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserShouldSaveAndReturnNewUser() {
        when(userRepository.existsByAuth0Id("auth0|new")).thenReturn(false);
        when(userRepository.existsByEmail("new.user@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.createUser("auth0|new", "new.user@test.com");

        assertEquals("auth0|new", created.getAuth0Id());
        assertEquals("new.user", created.getUsername());
        assertEquals("new.user@test.com", created.getEmail());
    }

    @Test
    void updateUsernameShouldThrowWhenUsernameAlreadyExistsForAnotherUser() {
        User current = User.builder().id("u1").auth0Id("auth0|1").username("old").build();
        when(userRepository.findByAuth0Id("auth0|1")).thenReturn(Optional.of(current));
        when(userRepository.existsByUsernameAndIdNot("taken", "u1")).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class,
                () -> userService.updateUsername("auth0|1", "taken"));

        assertEquals(ErrorCode.USERNAME_ALREADY_EXISTS, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUsernameShouldPersistAndReturnUpdatedUser() {
        User current = User.builder().id("u1").auth0Id("auth0|1").username("old").build();
        when(userRepository.findByAuth0Id("auth0|1")).thenReturn(Optional.of(current));
        when(userRepository.existsByUsernameAndIdNot("newname", "u1")).thenReturn(false);

        User updated = userService.updateUsername("auth0|1", "newname");

        assertSame(current, updated);
        assertEquals("newname", current.getUsername());
        verify(userRepository).save(eq(current));
    }
}
