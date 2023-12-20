package com.softnet.app.repository;

import com.softnet.app.entity.Exchange;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ExchangeRepository extends ReactiveMongoRepository<Exchange, String> {

    Mono<Exchange> findByOrigenCurrencyAndDestinyCurrency(String origenCurrency, String destinyCurrency);

}
