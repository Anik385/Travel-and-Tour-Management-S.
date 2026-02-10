package com.example.Travel.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // ADD THIS LINE
public class Flight extends BaseEntity {
    private String airline;
    private String flightNumber;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "airport", column = @Column(name = "departure_airport")),
            @AttributeOverride(name = "time", column = @Column(name = "departure_time")),
            @AttributeOverride(name = "date", column = @Column(name = "departure_date"))
    })
    private FlightSchedule departure;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "airport", column = @Column(name = "arrival_airport")),
            @AttributeOverride(name = "time", column = @Column(name = "arrival_time")),
            @AttributeOverride(name = "date", column = @Column(name = "arrival_date"))
    })
    private FlightSchedule arrival;

    private String duration;
    private Integer stops;

    @Embedded
    private Price price;

    private Integer seatsAvailable;
    private String aircraft;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_airport_code")
    @JsonIgnoreProperties({"departingFlights", "arrivingFlights"})  // ADD THIS LINE
    private Airport departureAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_airport_code")
    @JsonIgnoreProperties({"departingFlights", "arrivingFlights"})  // ADD THIS LINE
    private Airport arrivalAirport;
}