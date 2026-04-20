package edu.escuelaing.arep.monolith.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.escuelaing.arep.monolith.entity.Post;
import edu.escuelaing.arep.monolith.entity.Stream;
import edu.escuelaing.arep.monolith.entity.enums.StreamType;
import edu.escuelaing.arep.monolith.exception.ApiException;
import edu.escuelaing.arep.monolith.exception.ErrorCode;
import edu.escuelaing.arep.monolith.repository.PostRepository;
import edu.escuelaing.arep.monolith.repository.StreamRepository;

@ExtendWith(MockitoExtension.class)
class FeedServiceImplTest {

    @Mock
    private StreamRepository streamRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private FeedServiceImpl feedService;

    @Test
    void getPublicFeedShouldThrowWhenPublicStreamDoesNotExist() {
        when(streamRepository.findByType(StreamType.PUBLIC)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> feedService.getPublicFeed());

        assertEquals(ErrorCode.STREAM_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getPublicFeedShouldReturnPostsForPublicStream() {
        Stream stream = Stream.builder().id(1L).type(StreamType.PUBLIC).build();
        List<Post> expected = List.of(Post.builder().id(1L).content("hola").stream(stream).build());

        when(streamRepository.findByType(StreamType.PUBLIC)).thenReturn(Optional.of(stream));
        when(postRepository.findByStream(stream)).thenReturn(expected);

        List<Post> actual = feedService.getPublicFeed();

        assertSame(expected, actual);
        verify(postRepository).findByStream(stream);
    }
}
