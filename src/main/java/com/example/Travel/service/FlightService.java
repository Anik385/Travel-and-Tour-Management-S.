package com.example.Travel.service;

import com.example.Travel.dto.FlightDTO;
import com.example.Travel.dto.FlightScheduleDTO;
import com.example.Travel.dto.FlightSearchDTO;
import com.example.Travel.dto.PriceDTO;
import com.example.Travel.entity.Airport;
import com.example.Travel.entity.Flight;
import com.example.Travel.entity.FlightSchedule;
import com.example.Travel.entity.Price;
import com.example.Travel.repository.AirportRepository;
import com.example.Travel.repository.FlightRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirportRepository airportRepository;

    public List<FlightDTO> searchFlights(FlightSearchDTO searchDTO) {
        return flightRepository.searchFlights(
                        searchDTO.getFrom().toUpperCase(),
                        searchDTO.getTo().toUpperCase(),
                        searchDTO.getDepartureDate()
                ).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FlightDTO> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FlightDTO getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        return convertToDTO(flight);
    }

    public FlightDTO createFlight(FlightDTO flightDTO) {
        Flight flight = convertToEntity(flightDTO);
        Flight savedFlight = flightRepository.save(flight);
        return convertToDTO(savedFlight);
    }

    private FlightDTO convertToDTO(Flight flight) {
        FlightDTO dto = new FlightDTO();
        dto.setId(flight.getId());
        dto.setAirline(flight.getAirline());
        dto.setFlightNumber(flight.getFlightNumber());

        FlightScheduleDTO departureDTO = new FlightScheduleDTO();
        departureDTO.setAirport(flight.getDeparture().getAirport());
        departureDTO.setTime(flight.getDeparture().getTime());
        departureDTO.setDate(flight.getDeparture().getDate());
        dto.setDeparture(departureDTO);

        FlightScheduleDTO arrivalDTO = new FlightScheduleDTO();
        arrivalDTO.setAirport(flight.getArrival().getAirport());
        arrivalDTO.setTime(flight.getArrival().getTime());
        arrivalDTO.setDate(flight.getArrival().getDate());
        dto.setArrival(arrivalDTO);

        dto.setDuration(flight.getDuration());
        dto.setStops(flight.getStops());

        PriceDTO priceDTO = new PriceDTO();
        priceDTO.setEconomy(flight.getPrice().getEconomy());
        priceDTO.setBusiness(flight.getPrice().getBusiness());
        priceDTO.setFirstClass(flight.getPrice().getFirstClass());
        dto.setPrice(priceDTO);

        dto.setSeatsAvailable(flight.getSeatsAvailable());
        dto.setAircraft(flight.getAircraft());

        if (flight.getDepartureAirport() != null) {
            dto.setDepartureAirportCode(flight.getDepartureAirport().getCode());
        }
        if (flight.getArrivalAirport() != null) {
            dto.setArrivalAirportCode(flight.getArrivalAirport().getCode());
        }

        return dto;
    }

    private Flight convertToEntity(FlightDTO dto) {
        Flight flight = new Flight();
        flight.setAirline(dto.getAirline());
        flight.setFlightNumber(dto.getFlightNumber());

        FlightSchedule departure = new FlightSchedule();
        departure.setAirport(dto.getDeparture().getAirport());
        departure.setTime(dto.getDeparture().getTime());
        departure.setDate(dto.getDeparture().getDate());
        flight.setDeparture(departure);

        FlightSchedule arrival = new FlightSchedule();
        arrival.setAirport(dto.getArrival().getAirport());
        arrival.setTime(dto.getArrival().getTime());
        arrival.setDate(dto.getArrival().getDate());
        flight.setArrival(arrival);

        flight.setDuration(dto.getDuration());
        flight.setStops(dto.getStops());

        Price price = new Price();
        price.setEconomy(dto.getPrice().getEconomy());
        price.setBusiness(dto.getPrice().getBusiness());
        price.setFirstClass(dto.getPrice().getFirstClass());
        flight.setPrice(price);

        flight.setSeatsAvailable(dto.getSeatsAvailable());
        flight.setAircraft(dto.getAircraft());

        // Set airports if codes provided
        if (dto.getDepartureAirportCode() != null) {
            Airport departureAirport = airportRepository.findById(dto.getDepartureAirportCode())
                    .orElseThrow(() -> new RuntimeException("Departure airport not found"));
            flight.setDepartureAirport(departureAirport);
        }

        if (dto.getArrivalAirportCode() != null) {
            Airport arrivalAirport = airportRepository.findById(dto.getArrivalAirportCode())
                    .orElseThrow(() -> new RuntimeException("Arrival airport not found"));
            flight.setArrivalAirport(arrivalAirport);
        }

        return flight;
    }

    public FlightDTO updateFlight(Long id, FlightDTO flightDTO) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        // Update flight fields similar to createFlight
        Flight updatedFlight = convertToEntity(flightDTO);
        updatedFlight.setId(id); // Keep same ID
        Flight savedFlight = flightRepository.save(updatedFlight);
        return convertToDTO(savedFlight);
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }
}
