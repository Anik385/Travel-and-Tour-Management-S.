package com.example.Travel.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailySalesDTO {
    private LocalDate date;
    private Double salesAmount;
    private Long bookingsCount;
}
