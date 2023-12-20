package com.softnet.app.config.security;

import com.softnet.app.config.security.auth.UserPrincipal;
import com.softnet.app.entity.User;
import com.softnet.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    @Autowired
    UserService userService;

    public AuthenticationManager(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        var principal = (UserPrincipal) authentication.getPrincipal();

        //TODO add more user validation logic here.
        return userService.getUser(principal.getId())
                .filter(User::isEnabled)
                .switchIfEmpty(Mono.error(new Exception("User account is disabled.")))
                .map(user -> authentication);
    }
}
