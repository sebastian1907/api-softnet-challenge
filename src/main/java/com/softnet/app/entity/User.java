package com.softnet.app.entity;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection="users")
public class User {

    @Id
    private String id;
    @Indexed(unique = true)
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    private List<String> roles;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    private boolean enabled;
    private Date createdAt;
    private Date updatedAt;

}
