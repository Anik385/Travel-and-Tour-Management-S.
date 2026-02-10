package com.example.Travel.dto;

import lombok.Data;

@Data
public class TopCustomerDTO {
    private String customerName;
    private Long bookingCount;
    private Double totalSpent;
}
