package com.example.Travel.service;

import com.example.Travel.dto.ItineraryDayDTO;
import com.example.Travel.dto.TourDTO;
import com.example.Travel.entity.Category;
import com.example.Travel.entity.ItineraryDay;
import com.example.Travel.entity.Tour;
import com.example.Travel.repository.CategoryRepository;
import com.example.Travel.repository.TourRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TourService {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<TourDTO> getAllTours() {
        return tourRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TourDTO> searchTours(String searchTerm) {
        return tourRepository.searchTours(searchTerm).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TourDTO> getToursByDestination(String destination) {
        return tourRepository.findByDestinationContainingIgnoreCase(destination).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TourDTO getTourById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        return convertToDTO(tour);
    }

    public TourDTO createTour(TourDTO tourDTO) {
        Tour tour = convertToEntity(tourDTO);
        Tour savedTour = tourRepository.save(tour);
        return convertToDTO(savedTour);
    }

    private TourDTO convertToDTO(Tour tour) {
        TourDTO dto = new TourDTO();
        dto.setId(tour.getId());
        dto.setTitle(tour.getTitle());
        dto.setDestination(tour.getDestination());
        dto.setDuration(tour.getDuration());
        dto.setPrice(tour.getPrice());
        dto.setDescription(tour.getDescription());
        dto.setImages(tour.getImages());
        dto.setInclusions(tour.getInclusions());
        dto.setItinerary(tour.getItinerary().stream()
                .map(day -> {
                    ItineraryDayDTO dayDTO = new ItineraryDayDTO();
                    dayDTO.setDay(day.getDay());
                    dayDTO.setTitle(day.getTitle());
                    dayDTO.setDescription(day.getDescription());
                    return dayDTO;
                }).collect(Collectors.toList()));
        dto.setIsActive(tour.getIsActive());
        dto.setCategories(tour.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList()));
        return dto;
    }

    private Tour convertToEntity(TourDTO dto) {
        Tour tour = new Tour();
        tour.setTitle(dto.getTitle());
        tour.setDestination(dto.getDestination());
        tour.setDuration(dto.getDuration());
        tour.setPrice(dto.getPrice());
        tour.setDescription(dto.getDescription());
        tour.setImages(dto.getImages());
        tour.setInclusions(dto.getInclusions());
        tour.setItinerary(dto.getItinerary().stream()
                .map(dayDTO -> {
                    ItineraryDay day = new ItineraryDay();
                    day.setDay(dayDTO.getDay());
                    day.setTitle(dayDTO.getTitle());
                    day.setDescription(dayDTO.getDescription());
                    return day;
                }).collect(Collectors.toList()));
        tour.setIsActive(dto.getIsActive());

        // Set categories
        if (dto.getCategories() != null) {
            List<Category> categories = dto.getCategories().stream()
                    .map(categoryName -> categoryRepository.findByName(categoryName)
                            .orElseGet(() -> {
                                Category newCategory = new Category();
                                newCategory.setName(categoryName);
                                return categoryRepository.save(newCategory);
                            }))
                    .collect(Collectors.toList());
            tour.setCategories(categories);
        }

        return tour;
    }

    public TourDTO updateTour(Long id, TourDTO tourDTO) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        // Update fields
        tour.setTitle(tourDTO.getTitle());
        tour.setDestination(tourDTO.getDestination());
        tour.setDuration(tourDTO.getDuration());
        tour.setPrice(tourDTO.getPrice());
        tour.setDescription(tourDTO.getDescription());
        tour.setImages(tourDTO.getImages());
        tour.setInclusions(tourDTO.getInclusions());
        tour.setIsActive(tourDTO.getIsActive());

        // Update itinerary
        tour.setItinerary(tourDTO.getItinerary().stream()
                .map(dayDTO -> {
                    ItineraryDay day = new ItineraryDay();
                    day.setDay(dayDTO.getDay());
                    day.setTitle(dayDTO.getTitle());
                    day.setDescription(dayDTO.getDescription());
                    return day;
                }).collect(Collectors.toList()));

        // Update categories
        if (tourDTO.getCategories() != null) {
            List<Category> categories = tourDTO.getCategories().stream()
                    .map(categoryName -> categoryRepository.findByName(categoryName)
                            .orElseGet(() -> {
                                Category newCategory = new Category();
                                newCategory.setName(categoryName);
                                return categoryRepository.save(newCategory);
                            }))
                    .collect(Collectors.toList());
            tour.setCategories(categories);
        }

        Tour updatedTour = tourRepository.save(tour);
        return convertToDTO(updatedTour);
    }

    public void deleteTour(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        // Soft delete (set inactive) or hard delete:
        // tour.setIsActive(false); // Soft delete
        // tourRepository.save(tour);

        tourRepository.delete(tour); // Hard delete
    }
}