package com.example.Travel.repository;

import com.example.Travel.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirportRepository extends JpaRepository<Airport, String> {
    List<Airport> findByCityContainingIgnoreCase(String city);
    List<Airport> findByCountryContainingIgnoreCase(String country);
}
