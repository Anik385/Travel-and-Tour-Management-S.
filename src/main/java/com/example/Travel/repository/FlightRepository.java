package com.example.Travel.repository;


import com.example.Travel.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    // Existing methods
    @Query("SELECT f FROM Flight f WHERE f.departure.airport = :departureAirport AND f.arrival.airport = :arrivalAirport AND DATE(f.departure.date) = :departureDate")
    List<Flight> searchFlights(@Param("departureAirport") String departureAirport,
                               @Param("arrivalAirport") String arrivalAirport,
                               @Param("departureDate") LocalDate departureDate);

    List<Flight> findByAirlineContainingIgnoreCase(String airline);
    List<Flight> findByDepartureAirportCode(String airportCode);
    List<Flight> findByArrivalAirportCode(String airportCode);

    // New methods for AdminService
    @Query("SELECT COUNT(f) FROM Flight f")
    Long countAllFlights();

    @Query("SELECT f.airline, COUNT(f) FROM Flight f GROUP BY f.airline")
    List<Object[]> countFlightsByAirline();

    @Query("SELECT f FROM Flight f WHERE f.seatsAvailable < 10")
    List<Flight> findLowAvailabilityFlights();

    @Query("SELECT f FROM Flight f WHERE f.departure.date >= CURRENT_DATE ORDER BY f.departure.date ASC")
    List<Flight> findUpcomingFlights();
}
