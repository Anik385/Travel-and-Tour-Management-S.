package com.example.Travel.dto;

import lombok.Data;

import java.util.List;

@Data
public class TourDTO {
    private Long id;
    private String title;
    private String destination;
    private Integer duration;
    private Double price;
    private String description;
    private List<String> images;
    private List<String> inclusions;
    private List<ItineraryDayDTO> itinerary;
    private Boolean isActive;
    private List<String> categories;
}
