package com.fc.project.core.service;

import com.fc.project.core.domain.entity.User;
import com.fc.project.core.dto.UserCreateRequest;
import com.fc.project.core.repository.UserRepository;
import com.fc.project.core.util.Encryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final Encryptor encryptor;
    private final UserRepository userRepository;

    @Transactional
    public User create(UserCreateRequest userCreateRequest) {
        userRepository.findByEmail(userCreateRequest.getEmail())
                .ifPresent(user -> {
                    throw new RuntimeException("User Already Exists");
                });

        return userRepository.save(userCreateRequestToUser(userCreateRequest));
    }

    public Optional<User> authenticateUser(String email, String password){
        return userRepository.findByEmail(email)
                .map(user -> user.isMatch(encryptor, password) ? user : null);
    }

    private User userCreateRequestToUser(UserCreateRequest userCreateRequest) {
        return new User(
                userCreateRequest.getName(),
                userCreateRequest.getEmail(),
                encryptor.encrypt(userCreateRequest.getPassword()),
                userCreateRequest.getBirthDay()
        );
    }

    public User findByUserIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found."));
    }
}
