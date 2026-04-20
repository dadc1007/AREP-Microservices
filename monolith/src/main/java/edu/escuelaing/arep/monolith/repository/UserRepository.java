package edu.escuelaing.arep.monolith.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.escuelaing.arep.monolith.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    boolean existsByAuth0Id(String auth0Id);

    boolean existsByUsernameAndIdNot(String username, String id);

    Optional<User> findByAuth0Id(String auth0Id);
}
