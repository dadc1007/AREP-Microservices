package edu.escuelaing.arep.monolith.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.escuelaing.arep.monolith.entity.Post;
import edu.escuelaing.arep.monolith.entity.Stream;
import edu.escuelaing.arep.monolith.entity.enums.StreamType;
import edu.escuelaing.arep.monolith.exception.ApiException;
import edu.escuelaing.arep.monolith.exception.ErrorCode;
import edu.escuelaing.arep.monolith.repository.PostRepository;
import edu.escuelaing.arep.monolith.repository.StreamRepository;
import edu.escuelaing.arep.monolith.service.FeedService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final StreamRepository streamRepository;
    private final PostRepository postRepository;

    @Override
    public List<Post> getPublicFeed() {
        Stream stream = streamRepository.findByType(StreamType.PUBLIC)
                .orElseThrow(() -> new ApiException(ErrorCode.STREAM_NOT_FOUND));

        return postRepository.findByStream(stream);
    }
}
