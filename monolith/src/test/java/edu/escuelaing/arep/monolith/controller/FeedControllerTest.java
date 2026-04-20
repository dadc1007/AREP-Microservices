package edu.escuelaing.arep.monolith.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.escuelaing.arep.monolith.dto.response.PostResponse;
import edu.escuelaing.arep.monolith.entity.Post;
import edu.escuelaing.arep.monolith.service.FeedService;
import edu.escuelaing.arep.monolith.util.mapper.PostMapper;

@ExtendWith(MockitoExtension.class)
class FeedControllerTest {

    @Mock
    private FeedService feedService;

    @Mock
    private PostMapper postMapper;

    private FeedController feedController;

    @BeforeEach
    void setUp() {
        feedController = new FeedController(feedService, postMapper);
    }

    @Test
    void getPublicFeedShouldReturnOkWithMappedPosts() {
        List<Post> posts = List.of(Post.builder().id(1L).content("hola").build());
        List<PostResponse> response = List.of(PostResponse.builder().id(1L).content("hola").username("neo").build());

        when(feedService.getPublicFeed()).thenReturn(posts);
        when(postMapper.toPostResponseList(posts)).thenReturn(response);

        ResponseEntity<List<PostResponse>> entity = feedController.getPublicFeed();

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertSame(response, entity.getBody());
    }
}
