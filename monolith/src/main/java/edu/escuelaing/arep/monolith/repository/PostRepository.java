package edu.escuelaing.arep.monolith.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.escuelaing.arep.monolith.entity.Post;
import edu.escuelaing.arep.monolith.entity.Stream;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStream(Stream stream);

}
