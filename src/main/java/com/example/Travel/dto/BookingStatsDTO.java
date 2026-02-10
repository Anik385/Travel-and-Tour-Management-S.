package com.example.Travel.dto;

import lombok.Data;

import java.util.Map;

@Data
public class BookingStatsDTO {
    private Long totalBookings;
    private Double totalRevenue;
    private Long confirmedBookings;
    private Long cancelledBookings;
    private Double averageBookingValue;
    private Map<String, Long> bookingsByStatus;
}
