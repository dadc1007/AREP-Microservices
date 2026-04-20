package edu.escuelaing.arep.monolith.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import edu.escuelaing.arep.monolith.dto.response.UserResponse;
import edu.escuelaing.arep.monolith.entity.User;
import edu.escuelaing.arep.monolith.service.UserService;
import edu.escuelaing.arep.monolith.util.mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
class MeControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    private MeController meController;

    @BeforeEach
    void setUp() {
        meController = new MeController(userService, userMapper);
    }

    @Test
    void getCurrentUserShouldReturnOkWithUserResponse() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("auth0|1");

        User user = User.builder().id("u1").auth0Id("auth0|1").username("neo").build();
        UserResponse response = UserResponse.builder().id("u1").username("neo").email("neo@test.com").build();

        when(userService.getByAuth0Id("auth0|1")).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(response);

        ResponseEntity<UserResponse> entity = meController.getCurrentUser(jwt);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertSame(response, entity.getBody());
    }
}
