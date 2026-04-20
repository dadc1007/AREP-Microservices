package edu.escuelaing.arep.monolith.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.escuelaing.arep.monolith.entity.Stream;
import edu.escuelaing.arep.monolith.entity.enums.StreamType;

public interface StreamRepository extends JpaRepository<Stream, Long> {
    Optional<Stream> findByType(StreamType type);

}
