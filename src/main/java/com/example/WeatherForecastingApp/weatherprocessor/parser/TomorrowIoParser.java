package com.example.WeatherForecastingApp.weatherprocessor.parser;

import com.example.WeatherForecastingApp.weatherprocessor.model.HourlyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class TomorrowIoParser implements WeatherDataParser{

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WeatherData parse(String jsonData) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonData);
        JsonNode hourlyArray = rootNode.path("timelines").path("hourly");

        List<HourlyForecast> hourlyForecasts = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS);

        for (JsonNode hourlyNode : hourlyArray) {
            LocalDateTime timestamp = LocalDateTime.parse(hourlyNode.path("time").asText(), DateTimeFormatter.ISO_DATE_TIME)
                    .plusHours(2)
                    .truncatedTo(ChronoUnit.HOURS);

            if (!timestamp.isBefore(now)) {
                double temperature = hourlyNode.path("values").path("temperature").asDouble();
                hourlyForecasts.add(new HourlyForecast(timestamp, temperature));
            }
        }


        return new WeatherData("TomorrowIo", hourlyForecasts);

    }
}
