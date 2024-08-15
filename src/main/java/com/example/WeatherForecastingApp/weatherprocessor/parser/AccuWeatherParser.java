package com.example.WeatherForecastingApp.weatherprocessor.parser;

import com.example.WeatherForecastingApp.weatherprocessor.model.HourlyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class AccuWeatherParser implements WeatherDataParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WeatherData parse(String jsonData) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonData);

        List<HourlyForecast> hourlyForecasts = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        hourlyForecasts.add(new HourlyForecast(now, 0.0));

        for (JsonNode hourlyNode : rootNode) {
            LocalDateTime timestamp = LocalDateTime.parse(hourlyNode.path("DateTime").asText(), formatter).truncatedTo(ChronoUnit.HOURS);
            double temperature = hourlyNode.path("Temperature").path("Value").asDouble();
            hourlyForecasts.add(new HourlyForecast(timestamp, temperature));
        }

        return new WeatherData("AccuWeather", hourlyForecasts);
    }
}
