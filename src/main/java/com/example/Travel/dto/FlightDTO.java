package com.example.Travel.dto;

import lombok.Data;

@Data
public class FlightDTO {
    private Long id;
    private String airline;
    private String flightNumber;
    private FlightScheduleDTO departure;
    private FlightScheduleDTO arrival;
    private String duration;
    private Integer stops;
    private PriceDTO price;
    private Integer seatsAvailable;
    private String aircraft;
    private String departureAirportCode;
    private String arrivalAirportCode;
}