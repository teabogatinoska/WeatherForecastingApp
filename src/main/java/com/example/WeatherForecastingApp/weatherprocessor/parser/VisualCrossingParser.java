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
public class VisualCrossingParser implements WeatherDataParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WeatherData parse(String jsonData) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonData);
        JsonNode daysArray = rootNode.path("days");

        List<HourlyForecast> hourlyForecasts = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        for (JsonNode dayNode : daysArray) {
            String date = dayNode.path("datetime").asText();
            JsonNode hoursArray = dayNode.path("hours");

            for (JsonNode hourNode : hoursArray) {
                String time = hourNode.path("datetime").asText();
                LocalDateTime timestamp = LocalDateTime.parse(date + "T" + time, dateTimeFormatter).truncatedTo(ChronoUnit.HOURS);
                if (!timestamp.isBefore(now)) {
                    double temperature = hourNode.path("temp").asDouble();
                    double humidity = hourNode.path("humidity").asDouble();
                    double precipitationProbability =hourNode.path("precipprob").asDouble();
                    double windSpeed = hourNode.path("windspeed").asDouble();
                    hourlyForecasts.add(new HourlyForecast(timestamp, temperature, humidity, precipitationProbability, windSpeed));
                }
            }
        }

        return new WeatherData("VisualCrossing", hourlyForecasts);
    }
}
