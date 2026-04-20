package edu.escuelaing.arep.monolith.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.escuelaing.arep.monolith.dto.request.CreatePostRequest;
import edu.escuelaing.arep.monolith.entity.Post;
import edu.escuelaing.arep.monolith.entity.Stream;
import edu.escuelaing.arep.monolith.entity.User;
import edu.escuelaing.arep.monolith.entity.enums.StreamType;
import edu.escuelaing.arep.monolith.exception.ApiException;
import edu.escuelaing.arep.monolith.exception.ErrorCode;
import edu.escuelaing.arep.monolith.repository.PostRepository;
import edu.escuelaing.arep.monolith.repository.StreamRepository;
import edu.escuelaing.arep.monolith.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StreamRepository streamRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void createPostShouldThrowWhenContentExceedsLimit() {
        CreatePostRequest request = new CreatePostRequest("x".repeat(141));

        ApiException exception = assertThrows(ApiException.class,
                () -> postService.createPost("auth0|1", request));

        assertEquals(ErrorCode.POST_TOO_LONG, exception.getErrorCode());
        verifyNoInteractions(userRepository, streamRepository, postRepository);
    }

    @Test
    void createPostShouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByAuth0Id("auth0|missing")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> postService.createPost("auth0|missing", new CreatePostRequest("hola")));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createPostShouldThrowWhenPublicStreamDoesNotExist() {
        User user = User.builder().id("u1").build();
        when(userRepository.findByAuth0Id("auth0|1")).thenReturn(Optional.of(user));
        when(streamRepository.findByType(StreamType.PUBLIC)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> postService.createPost("auth0|1", new CreatePostRequest("hola")));

        assertEquals(ErrorCode.STREAM_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createPostShouldSaveAndReturnPost() {
        User user = User.builder().id("u1").username("neo").build();
        Stream stream = Stream.builder().id(1L).type(StreamType.PUBLIC).build();

        when(userRepository.findByAuth0Id("auth0|1")).thenReturn(Optional.of(user));
        when(streamRepository.findByType(StreamType.PUBLIC)).thenReturn(Optional.of(stream));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post created = postService.createPost("auth0|1", new CreatePostRequest("hola mundo"));

        assertEquals("hola mundo", created.getContent());
        assertSame(user, created.getUser());
        assertSame(stream, created.getStream());

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        assertEquals("hola mundo", captor.getValue().getContent());
    }
}
