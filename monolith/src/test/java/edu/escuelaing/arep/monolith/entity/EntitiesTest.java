package edu.escuelaing.arep.monolith.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import edu.escuelaing.arep.monolith.entity.enums.StreamType;

class EntitiesTest {

    @Test
    void userShouldSupportBuilderAndAccessors() {
        User user = User.builder()
                .id("u1")
                .auth0Id("auth0|1")
                .username("neo")
                .email("neo@test.com")
                .build();

        assertEquals("u1", user.getId());
        assertEquals("auth0|1", user.getAuth0Id());
        assertEquals("neo", user.getUsername());
        assertEquals("neo@test.com", user.getEmail());

        user.setUsername("trinity");
        assertEquals("trinity", user.getUsername());
    }

    @Test
    void postShouldSupportBuilderAndRelations() {
        User user = User.builder().id("u1").username("neo").build();
        Stream stream = Stream.builder().id(1L).type(StreamType.PUBLIC).name("Public").build();

        Post post = Post.builder()
                .id(10L)
                .content("hola")
                .user(user)
                .stream(stream)
                .build();

        assertEquals(10L, post.getId());
        assertEquals("hola", post.getContent());
        assertEquals("neo", post.getUser().getUsername());
        assertEquals(StreamType.PUBLIC, post.getStream().getType());
    }

    @Test
    void streamAndEnumShouldExposeExpectedValues() {
        Stream stream = Stream.builder().id(1L).type(StreamType.PUBLIC).name("Public").build();

        assertEquals(1L, stream.getId());
        assertEquals(StreamType.PUBLIC, stream.getType());
        assertEquals("PUBLIC", StreamType.PUBLIC.name());
        assertEquals(StreamType.PUBLIC, StreamType.valueOf("PUBLIC"));
        assertNotNull(stream.toString());
    }
}
