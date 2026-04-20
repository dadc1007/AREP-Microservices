package edu.escuelaing.arep.monolith.service.impl;

import org.springframework.stereotype.Service;

import edu.escuelaing.arep.monolith.entity.User;
import edu.escuelaing.arep.monolith.exception.ApiException;
import edu.escuelaing.arep.monolith.exception.ErrorCode;
import edu.escuelaing.arep.monolith.repository.UserRepository;
import edu.escuelaing.arep.monolith.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public User getByAuth0Id(String auth0Id) {
        return userRepository.findByAuth0Id(auth0Id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public User createUser(String auth0Id, String email) {
        if (auth0IdExists(auth0Id)) {
            return getByAuth0Id(auth0Id);
        } else if (emailExists(email)) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .auth0Id(auth0Id)
                .username(email.split("@")[0])
                .email(email)
                .build();

        user = userRepository.save(user);

        return user;
    }

    @Override
    public User updateUsername(String auth0Id, String username) {
        User user = getByAuth0Id(auth0Id);

        if (usernameExistsForAnotherUser(username, user.getId())) {
            throw new ApiException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        user.setUsername(username);
        userRepository.save(user);

        return user;
    }

    private boolean auth0IdExists(String auth0Id) {
        return userRepository.existsByAuth0Id(auth0Id);
    }

    private boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private boolean usernameExistsForAnotherUser(String username, String userId) {
        return userRepository.existsByUsernameAndIdNot(username, userId);
    }
}
