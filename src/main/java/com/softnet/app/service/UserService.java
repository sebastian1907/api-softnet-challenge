package com.softnet.app.service;

import com.softnet.app.entity.User;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> createUser(User user);
    Mono<User> getUser(String userId);
}
