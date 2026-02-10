package com.example.Travel.dto;

import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
}
