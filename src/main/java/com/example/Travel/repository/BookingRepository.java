package com.example.Travel.repository;

import com.example.Travel.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Existing methods
    List<Booking> findByUserId(Long userId);

    // New methods for AdminService
    List<Booking> findByBookingDateBetween(LocalDate start, LocalDate end);

    List<Booking> findByStatus(String status);

    // BookingRepository.java - Add this method:
    Optional<Booking> findByBookingReference(String bookingReference);

    Long countByStatus(String status);

    @Query("SELECT b FROM Booking b WHERE b.tour.id = :tourId")
    List<Booking> findByTourId(@Param("tourId") Long tourId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.tour.id = :tourId")
    Long countByTourId(@Param("tourId") Long tourId);

    @Query("SELECT b FROM Booking b WHERE b.flight.id = :flightId")
    List<Booking> findByFlightId(@Param("flightId") Long flightId);

    @Query("SELECT b FROM Booking b ORDER BY b.createdAt DESC")
    List<Booking> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.bookingDate DESC")
    List<Booking> findUserBookingsSorted(@Param("userId") Long userId);

    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.tour.id = :tourId AND b.status = 'CONFIRMED'")
    Double sumRevenueByTourId(@Param("tourId") Long tourId);

    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = 'CONFIRMED' AND b.bookingDate BETWEEN :start AND :end")
    Double sumRevenueBetweenDates(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(b), b.bookingDate FROM Booking b WHERE b.bookingDate BETWEEN :start AND :end GROUP BY b.bookingDate")
    List<Object[]> countBookingsByDate(@Param("start") LocalDate start, @Param("end") LocalDate end);
}

//package com.example.Travel.repository;
//
//import com.example.Travel.entity.Booking;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface BookingRepository extends JpaRepository<Booking, Long> {
//    List<Booking> findByUserId(Long userId);
//    Optional<Booking> findByBookingReference(String bookingReference);
//
//    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = 'CONFIRMED'")
//    List<Booking> findConfirmedBookingsByUser(@Param("userId") Long userId);
//}
