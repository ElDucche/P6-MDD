package com.elducche.userservice.service;

import com.elducche.userservice.exception.AlreadyExistException;
import com.elducche.userservice.model.User;
import com.elducche.userservice.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> register(User user) {
        return userRepository.findByEmail(user.getEmail())
                .flatMap(existingUser -> Mono.error(new AlreadyExistException("User with email " + user.getEmail() + " already exists.")))
                .then(userRepository.findByUsername(user.getUsername()))
                .flatMap(existingUser -> Mono.error(new AlreadyExistException("User with username " + user.getUsername() + " already exists.")))
                .switchIfEmpty(Mono.defer(() -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return userRepository.save(user);
                }))
                .cast(User.class);
    }

    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<Boolean> checkPassword(User user, String rawPassword) {
        return Mono.just(passwordEncoder.matches(rawPassword, user.getPassword()));
    }

    public Mono<User> updateUser(String email, User user) {
        return userRepository.findByEmail(email)
                .flatMap(existingUser -> {
                    existingUser.setUsername(user.getUsername());
                    existingUser.setEmail(user.getEmail());
                    return userRepository.save(existingUser);
                });
    }
}
