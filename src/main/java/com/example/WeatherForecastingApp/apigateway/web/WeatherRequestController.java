package com.example.WeatherForecastingApp.apigateway.web;

import com.example.WeatherForecastingApp.apigateway.dto.UserWeatherRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.kafka.core.KafkaTemplate;

@RestController
@RequestMapping("/api/weather")
public class WeatherRequestController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String REQUEST_TOPIC = "user-weather-request";

    @PostMapping("/request")
    public String requestWeatherData(@RequestBody UserWeatherRequestDto requestDto) {
        String event = createEvent(requestDto);
        kafkaTemplate.send(REQUEST_TOPIC, event);
        return "Weather request for " + requestDto.getLocation() + " by user " + requestDto.getUsername() + " has been received";
    }

    private String createEvent(UserWeatherRequestDto requestDto) {
        return "{\"username\": \"" + requestDto.getUsername() + "\", \"location\": \"" + requestDto.getLocation() + "\"}";
    }
}
