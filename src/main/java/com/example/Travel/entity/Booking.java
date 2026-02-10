package com.example.Travel.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingReference;

    @ManyToOne
    private User user;

    @ManyToOne
    private Tour tour;

    @ManyToOne
    private Flight flight;

    private LocalDate bookingDate;
    private LocalDate travelDate;
    private Integer numberOfGuests;
    private Double totalAmount;
    private String status; // CONFIRMED, CANCELLED, COMPLETED

    @CreationTimestamp
    private LocalDateTime createdAt;
}
