package com.softnet.app.service;

import com.softnet.app.dto.auth.TokenInfo;
import com.softnet.app.entity.User;
import reactor.core.publisher.Mono;

public interface JWTService {

    TokenInfo generateAccessToken(User user);
    Mono<TokenInfo> authenticate(String username, String password);
}
