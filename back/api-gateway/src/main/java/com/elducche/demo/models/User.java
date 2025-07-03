package com.elducche.demo.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("USERS")
@Data
public class User {
    @Id
    private Long id;
    private String pseudo;
    private String email;
    private String password;
}
