package com.example.Travel.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

    @Id
    @Column(unique = true)
    private String name; // "ROLE_USER", "ROLE_ADMIN"

    private String description;
}
