package edu.escuelaing.arep.monolith.util.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.escuelaing.arep.monolith.dto.response.UserResponse;
import edu.escuelaing.arep.monolith.entity.User;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toUserResponseShouldMapAllFields() {
        User user = User.builder().id("u1").username("neo").email("neo@test.com").build();

        UserResponse response = userMapper.toUserResponse(user);

        assertEquals("u1", response.id());
        assertEquals("neo", response.username());
        assertEquals("neo@test.com", response.email());
    }
}
