package com.softnet.app.service;

import com.softnet.app.entity.Exchange;
import com.softnet.app.entity.MoneyExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExchangeService {

    Mono<Exchange> save(Exchange exchange);
    Mono<Exchange> findByCurrencies(String origenCurrency, String destinyCurrency);
    Flux<Exchange> findAll();
    Mono<Exchange> findById(String id);
    Mono<MoneyExchange> saveMoneyExchange(MoneyExchange moneyExchange);
}
