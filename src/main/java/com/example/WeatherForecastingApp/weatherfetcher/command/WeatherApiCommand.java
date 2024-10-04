package com.example.WeatherForecastingApp.weatherfetcher.command;

import com.example.WeatherForecastingApp.common.dto.UserDataRequestDto;
import org.springframework.kafka.core.KafkaTemplate;

public interface WeatherApiCommand {
    void fetchWeatherData(UserDataRequestDto requestDto, KafkaTemplate<String, String> kafkaTemplate);
}
