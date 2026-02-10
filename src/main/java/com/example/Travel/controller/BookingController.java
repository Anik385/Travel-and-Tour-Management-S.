package com.example.Travel.controller;

import com.example.Travel.dto.BookingDTO;
import com.example.Travel.entity.User;
import com.example.Travel.service.BookingService;
import com.example.Travel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(
            @RequestBody BookingDTO bookingDTO,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Get current authenticated user
        String username = principal.getName();
        User user = userService.getUserEntityByUsername(username);

        // Override userId with authenticated user's ID (security)
        bookingDTO.setUserId(user.getId());

        BookingDTO booking = bookingService.createBooking(bookingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        List<BookingDTO> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<BookingDTO> getBookingByReference(@PathVariable String reference) {
        BookingDTO booking = bookingService.getBookingByReference(reference);
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long id,
                                                    @RequestBody BookingDTO bookingDTO) {
        BookingDTO updatedBooking = bookingService.updateBooking(id, bookingDTO);
        return ResponseEntity.ok(updatedBooking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/test-booking/{tourId}")
    public BookingDTO testBooking(@PathVariable Long tourId) {
        // Auto-create test booking for development
        BookingDTO testBooking = new BookingDTO();
        testBooking.setTourId(tourId);
        testBooking.setBookingReference("TRV-TEST-" + System.currentTimeMillis());
        testBooking.setStatus("CONFIRMED");
        return testBooking;
    }
}

//package com.example.Travel.controller;
//
//import com.example.Travel.dto.BookingDTO;
//import com.example.Travel.service.BookingService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/bookings")
//@CrossOrigin(origins = "http://localhost:4200")
//public class BookingController {
//
//    @Autowired
//    private BookingService bookingService;
//
//    @PostMapping
//    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
//        BookingDTO booking = bookingService.createBooking(bookingDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
//    }
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
//        List<BookingDTO> bookings = bookingService.getUserBookings(userId);
//        return ResponseEntity.ok(bookings);
//    }
//
//    @GetMapping("/reference/{reference}")
//    public ResponseEntity<BookingDTO> getBookingByReference(@PathVariable String reference) {
//        BookingDTO booking = bookingService.getBookingByReference(reference);
//        return ResponseEntity.ok(booking);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO) {
//        BookingDTO updatedBooking = bookingService.updateBooking(id, bookingDTO);
//        return ResponseEntity.ok(updatedBooking);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
//        bookingService.cancelBooking(id);
//        return ResponseEntity.noContent().build();
//    }
//}
