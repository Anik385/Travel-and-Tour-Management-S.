package com.example.Travel.controller;

import com.example.Travel.dto.TourDTO;
import com.example.Travel.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"}) // Add both origins
public class TourController {

    @Autowired
    private TourService tourService;

    @GetMapping
    public ResponseEntity<List<TourDTO>> getAllTours() {
        List<TourDTO> tours = tourService.getAllTours();
        return ResponseEntity.ok(tours);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TourDTO>> searchTours(@RequestParam String q) {
        List<TourDTO> tours = tourService.searchTours(q);
        return ResponseEntity.ok(tours);
    }

    @GetMapping("/destination/{destination}")
    public ResponseEntity<List<TourDTO>> getToursByDestination(@PathVariable String destination) {
        List<TourDTO> tours = tourService.getToursByDestination(destination);
        return ResponseEntity.ok(tours);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourDTO> getTour(@PathVariable Long id) {
        TourDTO tour = tourService.getTourById(id);
        return ResponseEntity.ok(tour);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TourDTO> createTour(@RequestBody TourDTO tourDTO) {
        TourDTO createdTour = tourService.createTour(tourDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTour);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TourDTO> updateTour(@PathVariable Long id, @RequestBody TourDTO tourDTO) {
        TourDTO updatedTour = tourService.updateTour(id, tourDTO);
        return ResponseEntity.ok(updatedTour);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }
}
