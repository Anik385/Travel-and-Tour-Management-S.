package com.example.Travel.dto;

import lombok.Data;

@Data
public class PopularTourDTO {
    private String tourName;
    private Long bookingsCount;
    private Double totalRevenue;
}
