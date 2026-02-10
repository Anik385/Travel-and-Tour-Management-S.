package com.example.Travel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Airport {
    @Id

    private String code; // IATA code like DAC, DXB
    private String name;
    private String city;
    private String country;

    @OneToMany(mappedBy = "departureAirport")
    @JsonIgnore  // ADD THIS LINE
    private List<Flight> departingFlights;

    @OneToMany(mappedBy = "arrivalAirport")
    @JsonIgnore  // ADD THIS LINE
    private List<Flight> arrivingFlights;
}
