package com.softnet.app.handler;

import com.softnet.app.config.security.auth.UserPrincipal;
import com.softnet.app.entity.Exchange;
import com.softnet.app.entity.MoneyExchange;
import com.softnet.app.service.ExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.Date;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@Slf4j
@PreAuthorize("hasRole('USER')")
public class ExchangeHandler {

    @Autowired
    ExchangeService exchangeService;

    @Autowired
    Validator validator;

    public Mono<ServerResponse> crear(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(Exchange.class).flatMap(e -> serverRequest.principal().cast(Authentication.class)
                .map(authentication -> (UserPrincipal) authentication.getPrincipal())
                .flatMap(u -> {
                    e.setUserId(u.getId());
                    e.setCreatedAt(new Date());
                    return Mono.just(e);
                })).flatMap(exchange -> {
            Errors errors = new BeanPropertyBindingResult(exchange, Exchange.class.getName());
            validator.validate(exchange, errors);
            if (errors.hasErrors()) {
                return Flux.fromIterable(errors.getFieldErrors())
                        .map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                        .collectList()
                        .flatMap(list -> ServerResponse.badRequest().body(fromValue(list)));
            } else {
                return exchangeService.findByCurrencies(exchange.getOrigenCurrency(), exchange.getDestinyCurrency())
                        .flatMap(ex -> ServerResponse.badRequest().body(fromValue(Collections.singletonMap("mensaje","El tipo de cambio ya existe"))))
                        .switchIfEmpty(exchangeService.save(exchange).flatMap(edb -> ServerResponse.created(URI.create("/api/exchange/".concat(edb.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(edb))));
                /*return exchangeService.save(exchange).flatMap(edb -> ServerResponse.created(URI.create("/api/exchange/".concat(edb.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(edb)));*/
            }
        });

    }

    public Mono<ServerResponse> listar(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(exchangeService.findAll(), Exchange.class);
    }

    public Mono<ServerResponse> ver(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");

        Mono<UserPrincipal> userPrincipal = serverRequest.principal().cast(Authentication.class)
                .map(authentication -> (UserPrincipal) authentication.getPrincipal());

        return exchangeService
                .findByCurrencies(serverRequest.pathVariable("origenCurrency"),
                        serverRequest.pathVariable("destinyCurrency"))
                .flatMap(p -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(p)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> editar(ServerRequest serverRequest) {

        return exchangeService.findById(serverRequest.pathVariable("id"))
                .zipWith(serverRequest.bodyToMono(Exchange.class), (db, req) -> {
                    db.setExchangeRate(req.getExchangeRate());
                    return db;
                }).flatMap(e -> ServerResponse.created(URI.create("/api/exchange/".concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(exchangeService.save(e), Exchange.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> cambiar(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(MoneyExchange.class).flatMap(moneyExchange -> serverRequest.principal().cast(Authentication.class)
                .map(authentication -> (UserPrincipal) authentication.getPrincipal())
                .flatMap(u -> {
                    moneyExchange.setUserId(u.getId());
                    return Mono.just(moneyExchange);
                })).flatMap(moneyExchange -> {

            Errors errors = new BeanPropertyBindingResult(moneyExchange, MoneyExchange.class.getName());
            validator.validate(moneyExchange, errors);
            if (errors.hasErrors()) {
                return Flux.fromIterable(errors.getFieldErrors())
                        .map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                        .collectList()
                        .flatMap(list -> ServerResponse.badRequest().body(fromValue(list)));
            } else {

                return exchangeService.findByCurrencies(moneyExchange.getOrigenCurrency(), moneyExchange.getDestinyCurrency())
                        .flatMap(exchange -> {
                            moneyExchange.setExchangeRate(exchange.getExchangeRate());
                            return exchangeService.saveMoneyExchange(moneyExchange);
                        }).flatMap(e -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(e)))
                        .switchIfEmpty(ServerResponse.notFound().build());
            }
        });

    }
}
