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
public class OpenMeteoParser implements WeatherDataParser{

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WeatherData parse(String jsonData) throws Exception {
        System.out.println("INSIDE OPENMETEO");
        JsonNode rootNode = objectMapper.readTree(jsonData);
        JsonNode times = rootNode.path("hourly").path("time");
        JsonNode temperatures = rootNode.path("hourly").path("temperature_2m");

        List<HourlyForecast> hourlyForecasts = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");


        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);


        for (int i = 0; i < times.size(); i++) {
            LocalDateTime timestamp = LocalDateTime.parse(times.get(i).asText(), formatter).truncatedTo(ChronoUnit.HOURS);
            if (!timestamp.isBefore(now)) {
                double temperature = temperatures.get(i).asDouble();
                hourlyForecasts.add(new HourlyForecast(timestamp, temperature));
            }
        }

        return new WeatherData("OpenMeteo", hourlyForecasts);
    }
}
