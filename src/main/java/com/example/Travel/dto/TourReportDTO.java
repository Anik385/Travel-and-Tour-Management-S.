package com.example.Travel.dto;

import lombok.Data;

// TourReportDTO.java
@Data
public class TourReportDTO {
    private Long tourId;
    private String tourTitle;
    private Long bookingsCount;
    private Double totalRevenue;
    private Double occupancyRate;
    private Double averageRating;
}
