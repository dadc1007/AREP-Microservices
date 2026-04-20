package edu.escuelaing.arep.monolith.util.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.escuelaing.arep.monolith.dto.response.PostResponse;
import edu.escuelaing.arep.monolith.entity.Post;
import edu.escuelaing.arep.monolith.entity.User;

class PostMapperTest {

    private final PostMapper postMapper = new PostMapper();

    @Test
    void toPostResponseShouldMapAllFields() {
        User user = User.builder().id("u1").username("neo").build();
        Post post = Post.builder().id(1L).content("hola").user(user).build();

        PostResponse response = postMapper.toPostResponse(post);

        assertEquals(1L, response.id());
        assertEquals("hola", response.content());
        assertEquals("neo", response.username());
    }

    @Test
    void toPostResponseListShouldMapEachPost() {
        User user = User.builder().id("u1").username("neo").build();
        List<Post> posts = List.of(
                Post.builder().id(1L).content("a").user(user).build(),
                Post.builder().id(2L).content("b").user(user).build());

        List<PostResponse> responses = postMapper.toPostResponseList(posts);

        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).id());
        assertEquals("a", responses.get(0).content());
        assertEquals("neo", responses.get(0).username());
        assertEquals(2L, responses.get(1).id());
    }
}
