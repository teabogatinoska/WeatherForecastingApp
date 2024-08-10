package com.example.WeatherForecastingApp.weatherfetcher.command;

import com.example.WeatherForecastingApp.apigateway.dto.UserWeatherRequestDto;
import org.springframework.kafka.core.KafkaTemplate;

public interface WeatherApiCommand {
    void fetchWeatherData(UserWeatherRequestDto requestDto, KafkaTemplate<String, String> kafkaTemplate);
}
