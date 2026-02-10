package com.example.Travel.dto;

import lombok.Data;
import java.util.List;

// DashboardStatsDTO.java
@Data
public class DashboardStatsDTO {
    private Long totalUsers;
    private Long totalTours;
    private Long totalFlights;
    private Long totalBookings;
    private Double monthlyRevenue;
    private Double yearlyRevenue;
    private List<RecentBookingDTO> recentBookings;
    private List<PopularTourDTO> popularTours;
}
