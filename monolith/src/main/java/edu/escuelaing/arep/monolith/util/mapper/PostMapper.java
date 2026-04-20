package edu.escuelaing.arep.monolith.util.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import edu.escuelaing.arep.monolith.dto.response.PostResponse;
import edu.escuelaing.arep.monolith.entity.Post;

@Component
public class PostMapper {
    public PostResponse toPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .username(post.getUser().getUsername())
                .build();
    }

    public List<PostResponse> toPostResponseList(List<Post> posts) {
        return posts.stream().map(this::toPostResponse).toList();
    }
}
