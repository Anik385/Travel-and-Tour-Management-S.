package com.example.Travel.controller;

import com.example.Travel.dto.FlightDTO;
import com.example.Travel.dto.FlightSearchDTO;
import com.example.Travel.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "http://localhost:4200")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @PostMapping("/search")
    public ResponseEntity<List<FlightDTO>> searchFlights(@RequestBody FlightSearchDTO searchDTO) {
        List<FlightDTO> flights = flightService.searchFlights(searchDTO);
        return ResponseEntity.ok(flights);
    }

    @GetMapping
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        List<FlightDTO> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightDTO> getFlight(@PathVariable Long id) {
        FlightDTO flight = flightService.getFlightById(id);
        return ResponseEntity.ok(flight);
    }
//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<FlightDTO> createFlight(@RequestBody FlightDTO flightDTO) {
        FlightDTO createdFlight = flightService.createFlight(flightDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFlight);
    }
//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<FlightDTO> updateFlight(@PathVariable Long id, @RequestBody FlightDTO flightDTO) {
        FlightDTO updatedFlight = flightService.updateFlight(id, flightDTO);
        return ResponseEntity.ok(updatedFlight);
    }
//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
}
