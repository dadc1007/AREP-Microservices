package edu.escuelaing.arep.monolith.service;

import edu.escuelaing.arep.monolith.entity.User;

public interface UserService {
    User getById(String id);

    User getByAuth0Id(String auth0Id);

    User createUser(String auth0Id, String email);

    User updateUsername(String auth0Id, String username);
}
