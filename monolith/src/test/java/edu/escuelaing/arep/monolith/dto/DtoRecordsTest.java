package edu.escuelaing.arep.monolith.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import edu.escuelaing.arep.monolith.dto.request.CreatePostRequest;
import edu.escuelaing.arep.monolith.dto.request.CreateUserRequest;
import edu.escuelaing.arep.monolith.dto.request.UpdateUsernameRequest;
import edu.escuelaing.arep.monolith.dto.response.ErrorResponse;
import edu.escuelaing.arep.monolith.dto.response.PostResponse;
import edu.escuelaing.arep.monolith.dto.response.UserResponse;

class DtoRecordsTest {

    @Test
    void requestRecordsShouldExposeValues() {
        CreateUserRequest createUser = new CreateUserRequest("neo@test.com");
        CreatePostRequest createPost = new CreatePostRequest("hola");
        UpdateUsernameRequest updateUsername = new UpdateUsernameRequest("neo");

        assertEquals("neo@test.com", createUser.email());
        assertEquals("hola", createPost.content());
        assertEquals("neo", updateUsername.username());
    }

    @Test
    void responseRecordsShouldBuildWithExpectedValues() {
        LocalDateTime now = LocalDateTime.now();

        UserResponse user = UserResponse.builder().id("u1").username("neo").email("neo@test.com").build();
        PostResponse post = PostResponse.builder().id(1L).content("hola").username("neo").build();
        ErrorResponse error = ErrorResponse.builder()
                .status(400)
                .error("BAD_REQUEST")
                .message("invalid")
                .timestamp(now)
                .build();

        assertEquals("u1", user.id());
        assertEquals("hola", post.content());
        assertEquals(400, error.status());
        assertNotNull(error.timestamp());
    }
}
