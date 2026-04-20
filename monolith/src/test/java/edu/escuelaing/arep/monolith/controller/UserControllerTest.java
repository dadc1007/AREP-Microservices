package edu.escuelaing.arep.monolith.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import edu.escuelaing.arep.monolith.dto.request.CreateUserRequest;
import edu.escuelaing.arep.monolith.dto.request.UpdateUsernameRequest;
import edu.escuelaing.arep.monolith.dto.response.UserResponse;
import edu.escuelaing.arep.monolith.entity.User;
import edu.escuelaing.arep.monolith.service.UserService;
import edu.escuelaing.arep.monolith.util.mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService, userMapper);
    }

    @Test
    void createUserShouldReturnCreatedResponse() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("auth0|1");

        User user = User.builder().id("u1").auth0Id("auth0|1").email("mail@test.com").build();
        UserResponse response = UserResponse.builder().id("u1").username("mail").email("mail@test.com").build();

        when(userService.createUser("auth0|1", "mail@test.com")).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(response);

        ResponseEntity<UserResponse> entity = userController.createUser(jwt, new CreateUserRequest("mail@test.com"));

        assertEquals(HttpStatus.CREATED, entity.getStatusCode());
        assertSame(response, entity.getBody());
    }

    @Test
    void updateUsernameShouldReturnOkResponse() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("auth0|1");

        User user = User.builder().id("u1").auth0Id("auth0|1").username("newname").build();
        UserResponse response = UserResponse.builder().id("u1").username("newname").email("mail@test.com").build();

        when(userService.updateUsername("auth0|1", "newname")).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(response);

        ResponseEntity<UserResponse> entity = userController.updateUsername(jwt, new UpdateUsernameRequest("newname"));

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertSame(response, entity.getBody());
        verify(userService).updateUsername("auth0|1", "newname");
    }
}
