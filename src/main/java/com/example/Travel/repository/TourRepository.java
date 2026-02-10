package com.example.Travel.repository;

import com.example.Travel.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    // Existing methods
    List<Tour> findByIsActiveTrue();
    List<Tour> findByDestinationContainingIgnoreCase(String destination);
    List<Tour> findByCategoriesName(String categoryName);

    @Query("SELECT t FROM Tour t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.destination) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Tour> searchTours(@Param("searchTerm") String searchTerm);

    // New methods for AdminService
    @Query("SELECT t FROM Tour t WHERE t.isActive = :isActive")
    List<Tour> findByActiveStatus(@Param("isActive") Boolean isActive);

    @Query("SELECT COUNT(t) FROM Tour t WHERE t.isActive = true")
    Long countActiveTours();

    @Query("SELECT t FROM Tour t ORDER BY t.createdAt DESC")
    List<Tour> findRecentTours(@Param("limit") int limit);

    @Query("SELECT t.destination, COUNT(b) FROM Tour t LEFT JOIN Booking b ON t.id = b.tour.id GROUP BY t.destination")
    List<Object[]> countBookingsByDestination();
}
//
//import com.example.Travel.entity.Tour;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface TourRepository extends JpaRepository<Tour, Long> {
//    List<Tour> findByIsActiveTrue();
//    List<Tour> findByDestinationContainingIgnoreCase(String destination);
//    List<Tour> findByCategoriesName(String categoryName);
//
//    @Query("SELECT t FROM Tour t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.destination) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
//    List<Tour> searchTours(@Param("searchTerm") String searchTerm);
//}
