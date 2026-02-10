package com.example.Travel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Embeddable
@Data
public class FlightSchedule {
    @Column(name = "airport_code")
    private String airport;
    private LocalDateTime time;
    private LocalDate date;
}
