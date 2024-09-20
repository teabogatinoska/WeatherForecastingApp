package com.example.WeatherForecastingApp.apigateway.web;

import com.example.WeatherForecastingApp.apigateway.service.LocationSearchService;
import com.example.WeatherForecastingApp.common.dto.LocationDto;
import com.example.WeatherForecastingApp.apigateway.service.GeoApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
public class LocationSearchController {
    @Autowired
    private GeoApiService geoApiService;

    @Autowired
    private LocationSearchService locationSearchService;

    public LocationSearchController(GeoApiService geoApiService, LocationSearchService locationSearchService) {
        this.geoApiService = geoApiService;
        this.locationSearchService = locationSearchService;
    }

    @GetMapping("/search")
    public List<LocationDto> searchLocation(@RequestParam String query) throws JsonProcessingException {
        return geoApiService.searchLocations(query);
    }

    @GetMapping("/recent-searches/{userId}")
    public List<LocationDto> getRecentSearches(@PathVariable Long userId) {
        return locationSearchService.getRecentSearches(userId);
    }

    @GetMapping("/favorite-locations/{userId}")
    public List<LocationDto> getFavoriteLocations(@PathVariable Long userId) {
        return locationSearchService.getFavoriteLocations(userId);
    }

    @PostMapping("/{userId}/favorite-location")
    public ResponseEntity<?> addFavoriteLocation(@PathVariable Long userId, @RequestBody LocationDto locationDto) {
        try {

            locationSearchService.addFavoriteLocation(userId, locationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Favorite location added successfully",
                    "userId", userId,
                    "location", locationDto
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "An unexpected error occurred"
            ));
        }
    }

    @DeleteMapping("/{userId}/favorite/{locationId}")
    public ResponseEntity<?> removeFavoriteLocation(@PathVariable Long userId, @PathVariable Long locationId) {
        try {
            locationSearchService.removeFavoriteLocation(userId, locationId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "Favorite location removed successfully",
                    "userId", userId,
                    "locationId", locationId
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "An unexpected error occurred"
            ));
        }
    }

}