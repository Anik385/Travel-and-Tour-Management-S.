package com.example.Travel.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FlightSearchDTO {
    private String from;
    private String to;
    private LocalDate departureDate;
    private Integer travelers;
    private String travelClass;
}