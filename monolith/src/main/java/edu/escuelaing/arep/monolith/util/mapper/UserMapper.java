package edu.escuelaing.arep.monolith.util.mapper;

import org.springframework.stereotype.Component;

import edu.escuelaing.arep.monolith.dto.response.UserResponse;
import edu.escuelaing.arep.monolith.entity.User;

@Component
public class UserMapper {
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
