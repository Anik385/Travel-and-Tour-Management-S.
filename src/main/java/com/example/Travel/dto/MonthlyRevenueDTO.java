package com.example.Travel.dto;

import lombok.Data;

@Data
public class MonthlyRevenueDTO {
    private String month;
    private Double revenue;
    private Long bookings;
}
