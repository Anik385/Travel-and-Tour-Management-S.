package com.example.Travel.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingDTO {
    private Long id;
    private String bookingReference;
    private Long userId;
    private Long tourId;
    private Long flightId;
    private LocalDate bookingDate;
    private LocalDate travelDate;
    private Integer numberOfGuests;
    private Double totalAmount;
    private String status;
}
