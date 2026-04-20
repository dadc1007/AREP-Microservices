package edu.escuelaing.arep.monolith.service;

import edu.escuelaing.arep.monolith.dto.request.CreatePostRequest;
import edu.escuelaing.arep.monolith.entity.Post;

public interface PostService {
    Post createPost(String auth0Id, CreatePostRequest request);
}
