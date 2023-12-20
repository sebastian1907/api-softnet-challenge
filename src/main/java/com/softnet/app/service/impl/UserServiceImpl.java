package com.softnet.app.service.impl;

import com.softnet.app.entity.User;
import com.softnet.app.repository.UserRepository;
import com.softnet.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Date;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<User> createUser(User user) {
        return userRepository.save(user.toBuilder()
                        .password(passwordEncoder.encode(user.getPassword()))
                        .roles(Collections.singletonList("ROLE_USER"))
                        .enabled(true)
                        .createdAt(new Date())
                        .build())
                .doOnSuccess(u -> log.info("Created new user with ID = " + u.getId()));
    }

    @Override
    public Mono<User> getUser(String userId) {
        return userRepository.findById(userId);
    }
}
