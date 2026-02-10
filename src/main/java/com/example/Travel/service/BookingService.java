package com.example.Travel.service;

import com.example.Travel.dto.BookingDTO;
import com.example.Travel.entity.Booking;
import com.example.Travel.entity.Flight;
import com.example.Travel.entity.Tour;
import com.example.Travel.entity.User;
import com.example.Travel.repository.BookingRepository;
import com.example.Travel.repository.FlightRepository;
import com.example.Travel.repository.TourRepository;
import com.example.Travel.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private FlightRepository flightRepository;

    public BookingDTO createBooking(BookingDTO bookingDTO) {
        User user = userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tour tour = null;
        if (bookingDTO.getTourId() != null) {
            tour = tourRepository.findById(bookingDTO.getTourId())
                    .orElseThrow(() -> new RuntimeException("Tour not found"));
        }

        Flight flight = null;
        if (bookingDTO.getFlightId() != null) {
            flight = flightRepository.findById(bookingDTO.getFlightId())
                    .orElseThrow(() -> new RuntimeException("Flight not found"));
        }

        Booking booking = new Booking();
        booking.setBookingReference(generateBookingReference());
        booking.setUser(user);
        booking.setTour(tour);
        booking.setFlight(flight);
        booking.setBookingDate(LocalDate.now());
        booking.setTravelDate(bookingDTO.getTravelDate());
        booking.setNumberOfGuests(bookingDTO.getNumberOfGuests());
        booking.setTotalAmount(bookingDTO.getTotalAmount());
        booking.setStatus("CONFIRMED");

        Booking savedBooking = bookingRepository.save(booking);
        return convertToDTO(savedBooking);
    }

    public List<BookingDTO> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookingDTO getBookingByReference(String reference) {
        Booking booking = bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new RuntimeException("Booking not found with reference: " + reference));
        return convertToDTO(booking);
    }

    private String generateBookingReference() {
        return "TRV-" + System.currentTimeMillis();
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setUserId(booking.getUser().getId());
        if (booking.getTour() != null) {
            dto.setTourId(booking.getTour().getId());
        }
        if (booking.getFlight() != null) {
            dto.setFlightId(booking.getFlight().getId());
        }
        dto.setBookingDate(booking.getBookingDate());
        dto.setTravelDate(booking.getTravelDate());
        dto.setNumberOfGuests(booking.getNumberOfGuests());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus());
        return dto;
    }
    public BookingDTO updateBooking(Long id, BookingDTO bookingDTO) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setTravelDate(bookingDTO.getTravelDate());
        booking.setNumberOfGuests(bookingDTO.getNumberOfGuests());
        booking.setTotalAmount(bookingDTO.getTotalAmount());
        booking.setStatus(bookingDTO.getStatus());

        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDTO(updatedBooking);
    }

    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }
}
