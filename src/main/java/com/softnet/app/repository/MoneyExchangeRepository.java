package com.softnet.app.repository;

import com.softnet.app.entity.MoneyExchange;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MoneyExchangeRepository extends ReactiveMongoRepository<MoneyExchange, String> {
}
