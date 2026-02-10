package com.example.Travel.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FlightScheduleDTO {
    private String airport;
    private LocalDateTime time;
    private LocalDate date;
}
