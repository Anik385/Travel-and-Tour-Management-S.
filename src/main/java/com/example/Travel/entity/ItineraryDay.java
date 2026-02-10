package com.example.Travel.entity;


import jakarta.persistence.Embeddable;
import lombok.Data;


@Embeddable
@Data
public class ItineraryDay {
    private Integer day;
    private String title;
    private String description;
}