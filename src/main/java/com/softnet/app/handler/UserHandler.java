package com.softnet.app.handler;

import com.softnet.app.dto.AuthResultDto;
import com.softnet.app.dto.UserDto;
import com.softnet.app.dto.UserLoginDto;
import com.softnet.app.entity.User;
import com.softnet.app.service.JWTService;
import com.softnet.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class UserHandler {

    @Autowired
    UserService userService;

    @Autowired
    JWTService jwtService;

    @Autowired
    Validator validator;

    public Mono<ServerResponse> crear(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(UserDto.class).flatMap(userDto -> {
                    Errors errors = new BeanPropertyBindingResult(userDto, UserDto.class.getName());
                    validator.validate(userDto, errors);
                    if (errors.hasErrors()) {
                        return Flux.fromIterable(errors.getFieldErrors())
                                .map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                                .collectList()
                                .flatMap(list -> ServerResponse.badRequest().body(fromValue(list)));
                    } else {
                        User user = new User();
                        user.setUsername(userDto.getUsername());
                        user.setPassword(userDto.getPassword());
                        user.setFirstName(userDto.getFirstName());
                        user.setLastName(userDto.getLastName());

                        return userService.createUser(user).flatMap(userdb -> ServerResponse.created(URI.create("/api/user/".concat(userdb.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(userdb)));
                    }

                })
                .onErrorResume(throwable -> ServerResponse.badRequest().body(fromValue(throwable.getMessage())));
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserLoginDto.class)
                .flatMap(userLoginDto -> jwtService.authenticate(userLoginDto.getUsername(), userLoginDto.getPassword()))
                .flatMap(tokenInfo -> Mono.just(AuthResultDto.builder()
                        .userId(tokenInfo.getUserId())
                        .token(tokenInfo.getToken())
                        .issuedAt(tokenInfo.getIssuedAt())
                        .expiresAt(tokenInfo.getExpiresAt())
                        .build()))
                .flatMap(authResultDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(authResultDto)));
    }

}
