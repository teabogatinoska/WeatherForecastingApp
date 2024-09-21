package com.example.WeatherForecastingApp.apigateway.web;

import com.example.WeatherForecastingApp.common.dto.LocationDto;
import com.example.WeatherForecastingApp.apigateway.dto.UserWeatherRequestDto;
import com.example.WeatherForecastingApp.apigateway.service.LocationSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.core.KafkaTemplate;

@RestController
@RequestMapping("/api/weather")
public class WeatherRequestController {

    @Autowired
    private KafkaTemplate<String, UserWeatherRequestDto> kafkaTemplate;

    @Autowired
    private LocationSearchService locationSearchService;

    private static final String REQUEST_TOPIC = "user-weather-request";

    @PostMapping("/request")
    public String requestWeatherData(@RequestBody UserWeatherRequestDto requestDto) {
        kafkaTemplate.send(REQUEST_TOPIC, requestDto);
        System.out.println(requestDto.toString());
        LocationDto locationDto = new LocationDto(requestDto.getLocation(), requestDto.getCountry());
        locationSearchService.updateRecentSearch(requestDto.getUserId(), locationDto);

        return "Weather request for " + requestDto.getLocation() + ", " + requestDto.getCountry() + " by user " + requestDto.getUsername() + " has been received";
    }
}
