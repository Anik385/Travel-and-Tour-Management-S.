package com.example.Travel.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Tour extends BaseEntity {
    private Long id;


    private String title;
    private String destination;
    private Integer duration;
    private Double price;
    private String description;

    private Integer maxCapacity = 50; // Add this line

    @ElementCollection
    private List<String> images = new ArrayList<>();

    @ElementCollection
    private List<String> inclusions;


    @ElementCollection
    @CollectionTable(name = "tour_itinerary", joinColumns = @JoinColumn(name = "tour_id"))
    private List<ItineraryDay> itinerary;

    private Boolean isActive = true;

    @ManyToMany
    @JoinTable(
            name = "tour_categories",
            joinColumns = @JoinColumn(name = "tour_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
}