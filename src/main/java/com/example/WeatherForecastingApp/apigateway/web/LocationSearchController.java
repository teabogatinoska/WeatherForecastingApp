package com.example.WeatherForecastingApp.apigateway.web;

import com.example.WeatherForecastingApp.common.dto.LocationDto;
import com.example.WeatherForecastingApp.apigateway.service.GeoApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/location")
public class LocationSearchController {
    @Autowired
    private GeoApiService geoApiService;

    public LocationSearchController(GeoApiService geoApiService) {
        this.geoApiService = geoApiService;
    }

    @GetMapping("/search")
    public List<LocationDto> searchLocation(@RequestParam String query) throws JsonProcessingException {
        return geoApiService.searchLocations(query);
    }
}
