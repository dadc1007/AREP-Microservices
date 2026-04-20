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

import edu.escuelaing.arep.monolith.dto.request.CreatePostRequest;
import edu.escuelaing.arep.monolith.dto.response.PostResponse;
import edu.escuelaing.arep.monolith.entity.Post;
import edu.escuelaing.arep.monolith.service.PostService;
import edu.escuelaing.arep.monolith.util.mapper.PostMapper;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private PostMapper postMapper;

    private PostController postController;

    @BeforeEach
    void setUp() {
        postController = new PostController(postService, postMapper);
    }

    @Test
    void createPostShouldReturnCreatedResponse() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("auth0|1");

        CreatePostRequest request = new CreatePostRequest("hola mundo");
        Post post = Post.builder().id(1L).content("hola mundo").build();
        PostResponse mapped = PostResponse.builder().id(1L).content("hola mundo").username("neo").build();

        when(postService.createPost("auth0|1", request)).thenReturn(post);
        when(postMapper.toPostResponse(post)).thenReturn(mapped);

        ResponseEntity<PostResponse> entity = postController.createPost(jwt, request);

        assertEquals(HttpStatus.CREATED, entity.getStatusCode());
        assertSame(mapped, entity.getBody());
    }
}
