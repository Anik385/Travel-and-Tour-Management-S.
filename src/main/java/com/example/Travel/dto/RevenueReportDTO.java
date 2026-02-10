package com.example.Travel.dto;

import lombok.Data;

import java.util.List;

// RevenueReportDTO.java
@Data
public class RevenueReportDTO {
    private String period; // "monthly", "yearly", "custom"
    private Double totalRevenue;
    private Double tourRevenue;
    private Double flightRevenue;
    private List<MonthlyRevenueDTO> monthlyData;
    private List<RevenueByCategoryDTO> revenueByCategory;

}
