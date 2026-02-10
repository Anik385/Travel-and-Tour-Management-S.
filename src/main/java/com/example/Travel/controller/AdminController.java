package com.example.Travel.controller;

import com.example.Travel.dto.*;
import com.example.Travel.entity.Role;
import com.example.Travel.service.AdminService;
import com.example.Travel.service.BookingService;
import com.example.Travel.service.FlightService;
import com.example.Travel.service.RoleService;
import com.example.Travel.service.TourService;
import com.example.Travel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private TourService tourService;

    @Autowired
    private FlightService flightService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    // ========== ROLE MANAGEMENT ==========
    @PostMapping("/roles/init")
    public ResponseEntity<String> initRoles() {
        roleService.initializeDefaultRoles();
        return ResponseEntity.ok("Roles initialized");
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @DeleteMapping("/roles/{name}")
    public ResponseEntity<Void> deleteRole(@PathVariable String name) {
        roleService.deleteRole(name);
        return ResponseEntity.noContent().build();
    }

    // ========== USER MANAGEMENT ==========
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserDTO> updateUserRole(@PathVariable Long id,
                                                  @RequestParam String role) {
        UserDTO updatedUser = userService.updateUserRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserDTO> updateUserStatus(@PathVariable Long id,
                                                    @RequestParam Boolean enabled) {
        // You'll need to add this method in UserService
        // UserDTO updatedUser = userService.updateUserStatus(id, enabled);
        // return ResponseEntity.ok(updatedUser);
        throw new UnsupportedOperationException("Implement updateUserStatus in UserService");
    }

    // ========== TOUR MANAGEMENT ==========
    @GetMapping("/tours")
    public ResponseEntity<List<TourDTO>> getAllToursAdmin() {
        // Get all tours including inactive ones
        List<TourDTO> tours = tourService.getAllTours(); // Modify to include inactive
        return ResponseEntity.ok(tours);
    }

    @PutMapping("/tours/{id}/status")
    public ResponseEntity<TourDTO> updateTourStatus(@PathVariable Long id,
                                                    @RequestParam Boolean active) {
        TourDTO tour = tourService.getTourById(id);
        tour.setIsActive(active);
        TourDTO updatedTour = tourService.updateTour(id, tour);
        return ResponseEntity.ok(updatedTour);
    }

    // ========== FLIGHT MANAGEMENT ==========
    @GetMapping("/flights")
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    // ========== BOOKING MANAGEMENT ==========
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        // You'll need to add getAllBookings method in BookingService
        throw new UnsupportedOperationException("Implement getAllBookings in BookingService");
    }

    @GetMapping("/bookings/stats")
    public ResponseEntity<BookingStatsDTO> getBookingStats() {
        // You'll need to create BookingStatsDTO and implement in AdminService
        throw new UnsupportedOperationException("Implement getBookingStats in AdminService");
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<BookingDTO> updateBookingStatus(@PathVariable Long id,
                                                          @RequestParam String status) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setStatus(status);
        BookingDTO updatedBooking = bookingService.updateBooking(id, bookingDTO);
        return ResponseEntity.ok(updatedBooking);
    }

    // ========== DASHBOARD & REPORTS ==========
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        // Create DashboardStatsDTO and implement in AdminService
        throw new UnsupportedOperationException("Implement getDashboardStats in AdminService");
    }

    @GetMapping("/reports/sales")
    public ResponseEntity<SalesReportDTO> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        // Create SalesReportDTO and implement in AdminService
        throw new UnsupportedOperationException("Implement getSalesReport in AdminService");
    }

    @GetMapping("/reports/popular-tours")
    public ResponseEntity<List<TourReportDTO>> getPopularToursReport() {
        // Create TourReportDTO and implement in AdminService
        throw new UnsupportedOperationException("Implement getPopularToursReport in AdminService");
    }

    @GetMapping("/reports/revenue")
    public ResponseEntity<RevenueReportDTO> getRevenueReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        // Create RevenueReportDTO and implement in AdminService
        throw new UnsupportedOperationException("Implement getRevenueReport in AdminService");
    }
}

//package com.example.Travel.controller;
//
//
//import com.example.Travel.entity.Role;
//import com.example.Travel.service.RoleService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/admin")
//@CrossOrigin(origins = "http://localhost:4200")
//public class AdminController {
//
//    @Autowired
//    private RoleService roleService;
//
//    @PostMapping("/roles/init")
//    public String initRoles() {
//        roleService.initializeDefaultRoles();
//        return "Roles initialized";
//    }
//
//    @GetMapping("/roles")
//    public List<Role> getAllRoles() {
//        return roleService.getAllRoles();
//    }
//}
