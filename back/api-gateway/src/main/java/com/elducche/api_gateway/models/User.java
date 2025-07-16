package com.elducche.api_gateway.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("USERS")
@Data
public class User {
    @Id
    private Long id;
    private String username;
    private String email;
    private String password;
}
