package com.example.Travel.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

// SalesReportDTO.java
@Data
public class SalesReportDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalSales;
    private Long totalBookings;
    private List<DailySalesDTO> dailySales;
    private List<TopCustomerDTO> topCustomers;
}
