package com.softnet.app.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection="money_exchange")
public class MoneyExchange {

    @Id
    private String id;
    @NotEmpty
    private String origenCurrency;
    @NotEmpty
    private String destinyCurrency;
    private Double exchangeRate;
    @NotNull
    private Double amount;
    private Double amountExchange;
    @CreatedDate
    private Date createdAt;
    private String userId;

}
