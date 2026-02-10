package com.example.Travel.controller;

import com.example.Travel.entity.Airport;
import com.example.Travel.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
@CrossOrigin(origins = "http://localhost:4200")
public class AirportController {

    @Autowired
    private AirportService airportService;

    @PostMapping
    public ResponseEntity<Airport> createAirport(@RequestBody Airport airport) {
        Airport created = airportService.createAirport(airport);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Airport>> getAllAirports() {
        return ResponseEntity.ok(airportService.getAllAirports());
    }

    @GetMapping("/{code}")
    public ResponseEntity<Airport> getAirport(@PathVariable String code) {
        return ResponseEntity.ok(airportService.getAirportByCode(code));
    }

    @PutMapping("/{code}")
    public ResponseEntity<Airport> updateAirport(@PathVariable String code, @RequestBody Airport airport) {
        return ResponseEntity.ok(airportService.updateAirport(code, airport));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteAirport(@PathVariable String code) {
        airportService.deleteAirport(code);
        return ResponseEntity.noContent().build();
    }
}
