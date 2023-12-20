package com.softnet.app.service.impl;

import com.softnet.app.dto.auth.TokenInfo;
import com.softnet.app.entity.User;
import com.softnet.app.repository.UserRepository;
import com.softnet.app.service.JWTService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class JWTServiceImpl implements JWTService {

    final private UserRepository userRepository;
    final private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String defaultExpirationTimeInSecondsConf;

    public JWTServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TokenInfo generateAccessToken(User user) {
        var claims = new HashMap<String, Object>() {{
            put("role", user.getRoles());
        }};

        return doGenerateToken(claims, user.getUsername(), user.getId());
    }

    private TokenInfo doGenerateToken(Map<String, Object> claims, String issuer, String subject) {
        var expirationTimeInMilliseconds = Long.parseLong(defaultExpirationTimeInSecondsConf) * 1000;
        var expirationDate = new Date(new Date().getTime() + expirationTimeInMilliseconds);

        return doGenerateToken(expirationDate, claims, issuer, subject);
    }

    private TokenInfo doGenerateToken(Date expirationDate, Map<String, Object> claims, String issuer, String subject) {
        var createdDate = new Date();
        var token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setId(UUID.randomUUID().toString())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
                .compact();

        return TokenInfo.builder()
                .token(token)
                .issuedAt(createdDate)
                .expiresAt(expirationDate)
                .build();
    }

    @Override
    public Mono<TokenInfo> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .flatMap(user -> {
                    String pass = passwordEncoder.encode(password);
                    if (!user.isEnabled())
                        return Mono.error(new Exception("Account disabled."));

                    if (!passwordEncoder.matches(password, user.getPassword()))
                        return Mono.error(new Exception("Invalid user password!"));

                    return Mono.just(generateAccessToken(user).toBuilder()
                            .userId(user.getId())
                            .build());
                })
                .switchIfEmpty(Mono.error(new Exception("Invalid user, " + username + " is not registered.")));
    }

}
