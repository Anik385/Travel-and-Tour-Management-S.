package com.example.Travel.service;

import com.example.Travel.dto.*;
import com.example.Travel.entity.Booking;
import com.example.Travel.entity.Tour;
import com.example.Travel.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private TourRepository tourRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private FlightRepository flightRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalUsers(userRepo.count());
        stats.setTotalTours(tourRepo.count());
        stats.setTotalFlights(flightRepo.count());
        stats.setTotalBookings(bookingRepo.count());
        stats.setMonthlyRevenue(calculateMonthlyRevenue());
        stats.setYearlyRevenue(calculateYearlyRevenue());
        stats.setRecentBookings(getRecentBookings(10));
        stats.setPopularTours(getPopularTours(5));
        return stats;
    }

    public SalesReportDTO getSalesReport(LocalDate start, LocalDate end) {
        SalesReportDTO report = new SalesReportDTO();
        report.setStartDate(start);
        report.setEndDate(end);

        List<Booking> bookings = bookingRepo.findByBookingDateBetween(start, end);
        report.setTotalSales(bookings.stream().mapToDouble(Booking::getTotalAmount).sum());
        report.setTotalBookings((long) bookings.size());
        report.setDailySales(calculateDailySales(start, end));
        report.setTopCustomers(getTopCustomers(start, end, 10));

        return report;
    }

    public List<TourReportDTO> getPopularToursReport() {
        return tourRepo.findAll().stream()
                .map(tour -> {
                    TourReportDTO dto = new TourReportDTO();
                    dto.setTourId(tour.getId());
                    dto.setTourTitle(tour.getTitle());

                    Long bookings = bookingRepo.countByTourId(tour.getId());
                    dto.setBookingsCount(bookings);

                    Double revenue = bookingRepo.findByTourId(tour.getId()).stream()
                            .mapToDouble(Booking::getTotalAmount)
                            .sum();
                    dto.setTotalRevenue(revenue);

                    // Calculate occupancy rate (example: based on max capacity)
                    Double occupancyRate = 0.0;
                    if (tour.getMaxCapacity() != null && tour.getMaxCapacity() > 0 && bookings > 0) {
                        occupancyRate = (bookings.doubleValue() / tour.getMaxCapacity()) * 100;
                    }
                    dto.setOccupancyRate(occupancyRate);

                    return dto;
                })
                .sorted((a, b) -> b.getBookingsCount().compareTo(a.getBookingsCount()))
                .limit(10)
                .collect(Collectors.toList());
    }

    public RevenueReportDTO getRevenueReport(Integer year, Integer month) {
        RevenueReportDTO report = new RevenueReportDTO();

        if (month != null && year != null) {
            report.setPeriod("monthly");
            report.setTotalRevenue(calculateRevenueForMonth(year, month)); // Changed from setMonthlyRevenue
        } else if (year != null) {
            report.setPeriod("yearly");
            report.setTotalRevenue(calculateRevenueForYear(year));
        } else {
            report.setPeriod("custom");
            report.setTotalRevenue(calculateTotalRevenue());
        }

        report.setTourRevenue(calculateTourRevenue());
        report.setFlightRevenue(calculateFlightRevenue());
        report.setMonthlyData(getMonthlyRevenueData(year));
        report.setRevenueByCategory(getRevenueByCategory());

        return report;
    }

    public BookingStatsDTO getBookingStats() {
        BookingStatsDTO stats = new BookingStatsDTO();

        stats.setTotalBookings(bookingRepo.count());
        stats.setTotalRevenue(bookingRepo.findAll().stream()
                .mapToDouble(Booking::getTotalAmount).sum());

        stats.setConfirmedBookings(bookingRepo.countByStatus("CONFIRMED"));
        stats.setCancelledBookings(bookingRepo.countByStatus("CANCELLED"));

        if (stats.getTotalBookings() > 0) {
            stats.setAverageBookingValue(stats.getTotalRevenue() / stats.getTotalBookings());
        }

        Map<String, Long> statusCounts = bookingRepo.findAll().stream()
                .collect(Collectors.groupingBy(Booking::getStatus, Collectors.counting()));
        stats.setBookingsByStatus(statusCounts);

        return stats;
    }

    // Helper Methods
    private Double calculateMonthlyRevenue() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now();
        return bookingRepo.findByBookingDateBetween(start, end).stream()
                .mapToDouble(Booking::getTotalAmount).sum();
    }

    private Double calculateYearlyRevenue() {
        LocalDate start = LocalDate.now().withDayOfYear(1);
        LocalDate end = LocalDate.now();
        return bookingRepo.findByBookingDateBetween(start, end).stream()
                .mapToDouble(Booking::getTotalAmount).sum();
    }

    private Double calculateRevenueForMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return bookingRepo.findByBookingDateBetween(start, end).stream()
                .mapToDouble(Booking::getTotalAmount).sum();
    }

    private Double calculateRevenueForYear(int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return bookingRepo.findByBookingDateBetween(start, end).stream()
                .mapToDouble(Booking::getTotalAmount).sum();
    }

    private Double calculateTotalRevenue() {
        return bookingRepo.findAll().stream()
                .mapToDouble(Booking::getTotalAmount).sum();
    }

    private Double calculateTourRevenue() {
        return bookingRepo.findAll().stream()
                .filter(booking -> booking.getTour() != null)
                .mapToDouble(Booking::getTotalAmount).sum();
    }

    private Double calculateFlightRevenue() {
        return bookingRepo.findAll().stream()
                .filter(booking -> booking.getFlight() != null)
                .mapToDouble(Booking::getTotalAmount).sum();
    }

    private List<RecentBookingDTO> getRecentBookings(int limit) {
        return bookingRepo.findTop10ByOrderByCreatedAtDesc().stream()
                .map(booking -> {
                    RecentBookingDTO dto = new RecentBookingDTO();
                    dto.setBookingReference(booking.getBookingReference());
                    if (booking.getUser() != null) {
                        dto.setCustomerName(booking.getUser().getFirstName() + " " + booking.getUser().getLastName());
                    }
                    if (booking.getTour() != null) {
                        dto.setTourName(booking.getTour().getTitle());
                    } else if (booking.getFlight() != null) {
                        dto.setTourName(booking.getFlight().getAirline() + " - " + booking.getFlight().getFlightNumber());
                    }
                    dto.setAmount(booking.getTotalAmount());
                    dto.setBookingDate(booking.getBookingDate());
                    return dto;
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<PopularTourDTO> getPopularTours(int limit) {
        return getPopularToursReport().stream()
                .map(tour -> {
                    PopularTourDTO dto = new PopularTourDTO();
                    dto.setTourName(tour.getTourTitle());
                    dto.setBookingsCount(tour.getBookingsCount());
                    dto.setTotalRevenue(tour.getTotalRevenue());
                    return dto;
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<DailySalesDTO> calculateDailySales(LocalDate start, LocalDate end) {
        // Implementation for daily sales breakdown
        return bookingRepo.findByBookingDateBetween(start, end).stream()
                .collect(Collectors.groupingBy(Booking::getBookingDate,
                        Collectors.summarizingDouble(Booking::getTotalAmount)))
                .entrySet().stream()
                .map(entry -> {
                    DailySalesDTO dto = new DailySalesDTO();
                    dto.setDate(entry.getKey());
                    dto.setSalesAmount(entry.getValue().getSum());
                    dto.setBookingsCount(entry.getValue().getCount());
                    return dto;
                })
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
    }

    private List<TopCustomerDTO> getTopCustomers(LocalDate start, LocalDate end, int limit) {
        return bookingRepo.findByBookingDateBetween(start, end).stream()
                .filter(booking -> booking.getUser() != null)
                .collect(Collectors.groupingBy(Booking::getUser,
                        Collectors.summarizingDouble(Booking::getTotalAmount)))
                .entrySet().stream()
                .map(entry -> {
                    TopCustomerDTO dto = new TopCustomerDTO();
                    dto.setCustomerName(entry.getKey().getFirstName() + " " + entry.getKey().getLastName());
                    dto.setTotalSpent(entry.getValue().getSum());
                    dto.setBookingCount(entry.getValue().getCount());
                    return dto;
                })
                .sorted((a, b) -> Double.compare(b.getTotalSpent(), a.getTotalSpent()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<MonthlyRevenueDTO> getMonthlyRevenueData(Integer year) {
        // Implementation for monthly revenue data
        return List.of(); // Placeholder
    }

    private List<RevenueByCategoryDTO> getRevenueByCategory() {
        // Implementation for revenue by category
        return List.of(); // Placeholder
    }
}