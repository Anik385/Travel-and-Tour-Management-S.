package com.example.Travel.dto;

import lombok.Data;

import java.time.LocalDate;

// Supporting DTOs
@Data
public class RecentBookingDTO {
    private String bookingReference;
    private String customerName;
    private String tourName;
    private Double amount;
    private LocalDate bookingDate;
}
