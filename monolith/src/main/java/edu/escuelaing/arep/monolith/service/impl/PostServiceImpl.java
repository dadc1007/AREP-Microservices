package edu.escuelaing.arep.monolith.service.impl;

import org.springframework.stereotype.Service;

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
import edu.escuelaing.arep.monolith.service.PostService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final UserRepository userRepository;
    private final StreamRepository streamRepository;
    private final PostRepository postRepository;

    @Override
    public Post createPost(String auth0Id, CreatePostRequest request) {
        if (request.content().length() > 140) {
            throw new ApiException(ErrorCode.POST_TOO_LONG);
        }

        User user = userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        Stream stream = streamRepository.findByType(StreamType.PUBLIC)
                .orElseThrow(() -> new ApiException(ErrorCode.STREAM_NOT_FOUND));
        Post post = Post.builder()
                .content(request.content())
                .user(user)
                .stream(stream)
                .build();

        postRepository.save(post);

        return post;
    }
}
