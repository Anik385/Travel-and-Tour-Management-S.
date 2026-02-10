package com.example.Travel.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Price {
    private Double economy;
    private Double business;
    private Double firstClass;
}
