package com.example.Travel.service;

import com.example.Travel.entity.Airport;
import com.example.Travel.repository.AirportRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AirportService {

    @Autowired
    private AirportRepository airportRepository;

    public Airport createAirport(Airport airport) {
        return airportRepository.save(airport);
    }

    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public Airport getAirportByCode(String code) {
        return airportRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Airport not found"));
    }

    public Airport updateAirport(String code, Airport airportDetails) {
        Airport airport = getAirportByCode(code);
        airport.setName(airportDetails.getName());
        airport.setCity(airportDetails.getCity());
        airport.setCountry(airportDetails.getCountry());
        return airportRepository.save(airport);
    }

    public void deleteAirport(String code) {
        airportRepository.deleteById(code);
    }
}
