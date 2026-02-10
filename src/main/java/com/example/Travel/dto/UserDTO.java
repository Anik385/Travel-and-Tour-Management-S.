package com.example.Travel.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String role;
    private Boolean enabled;
}
