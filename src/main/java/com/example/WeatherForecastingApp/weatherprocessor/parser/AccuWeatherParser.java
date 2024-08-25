package com.example.WeatherForecastingApp.weatherprocessor.parser;

import com.example.WeatherForecastingApp.weatherprocessor.model.HourlyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Paris")).truncatedTo(ChronoUnit.MINUTES);

        for (JsonNode hourlyNode : rootNode) {

            OffsetDateTime offsetDateTime = OffsetDateTime.parse(hourlyNode.path("DateTime").asText(), formatter);
            ZonedDateTime originalZonedDateTime = offsetDateTime.toZonedDateTime();
            ZonedDateTime timestampInCET = originalZonedDateTime.withZoneSameInstant(ZoneId.of("Europe/Paris"));

            if (!timestampInCET.isBefore(now)) {

                double temperature = hourlyNode.path("Temperature").path("Value").asDouble();
                double humidity = hourlyNode.path("RelativeHumidity").asDouble();
                double precipitationProbability = hourlyNode.path("PrecipitationProbability").asDouble();
                double windSpeed = hourlyNode.path("Wind").path("Speed").path("Value").asDouble();
                hourlyForecasts.add(new HourlyForecast(timestampInCET.toLocalDateTime(), temperature, humidity, precipitationProbability, windSpeed));
            }
        }

        return new WeatherData("AccuWeather", hourlyForecasts);
    }


}
