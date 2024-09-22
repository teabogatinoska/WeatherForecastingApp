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
public class WeatherApiParser implements WeatherDataParser{

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WeatherData parse(String jsonData) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonData);
        JsonNode forecastDayArray = rootNode.path("forecast").path("forecastday");

        List<HourlyForecast> hourlyForecasts = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        for (JsonNode forecastDayNode : forecastDayArray) {
            JsonNode hourArray = forecastDayNode.path("hour");

            for (JsonNode hourNode : hourArray) {
                LocalDateTime timestamp = LocalDateTime.parse(hourNode.path("time").asText(), formatter).truncatedTo(ChronoUnit.HOURS);
                if (!timestamp.isBefore(now)) {
                    double temperature = hourNode.path("temp_c").asDouble();
                    double humidity = hourNode.path("humidity").asDouble();
                    double precipitationProbability = hourNode.path("chance_of_rain").asDouble();
                    double windSpeed = hourNode.path("wind_kph").asDouble();
                    String description = hourNode.path("condition").path("text").asText();
                    System.out.println("Description: " + timestamp + " " + description);
                    hourlyForecasts.add(new HourlyForecast(timestamp, temperature, humidity, precipitationProbability, windSpeed, description));
                }
            }
        }

        return new WeatherData("WeatherAPI", hourlyForecasts);
    }
}
