package edu.escuelaing.arep.monolith.service;

import java.util.List;

import edu.escuelaing.arep.monolith.entity.Post;

public interface FeedService {
    List<Post> getPublicFeed();
}
