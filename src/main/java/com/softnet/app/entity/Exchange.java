package com.softnet.app.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection="exchange")
public class Exchange {

    @Id
    private String id;
    @NotEmpty
    private String origenCurrency;
    @NotEmpty
    private String destinyCurrency;
    @NotNull
    private Double exchangeRate;
    @CreatedDate
    private Date createdAt;
    private String userId;

}
