package com.example.Travel.dto;


import lombok.Data;

@Data
public class RevenueByCategoryDTO {
    private String category;
    private Double revenue;
    private Double percentage;
}
