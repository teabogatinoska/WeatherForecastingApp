package com.example.WeatherForecastingApp.weatherprocessor;

import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedDailyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedHourlyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.HourlyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.WeatherData;
import com.example.WeatherForecastingApp.weatherprocessor.parser.*;
import com.example.WeatherForecastingApp.weatherprocessor.processor.WeatherProcessorManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Service
public class WeatherDataAggregator {

    private final Map<String, WeatherDataParser> parsers;

    private final Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts;

    private final Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts;

    private final WeatherProcessorManager weatherProcessorManager;
    private final Set<String> processedApis;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public WeatherDataAggregator(List<WeatherDataParser> parserList, WeatherProcessorManager weatherProcessorManager) {
        System.out.println("INSIDE CONSTRUCTOR");
        this.parsers = new HashMap<>();
        this.combinedHourlyForecasts = new TreeMap<>();
        this.combinedDailyForecasts = new TreeMap<>();
        this.weatherProcessorManager = weatherProcessorManager;
        this.processedApis = new HashSet<>();

        for (WeatherDataParser parser : parserList) {
            if (parser instanceof OpenMeteoParser) {
                parsers.put("weather-api1-data", parser);
            } else if (parser instanceof TomorrowIoParser) {
                parsers.put("weather-api2-data", parser);
            } else if (parser instanceof WeatherApiParser) {
                parsers.put("weather-api3-data", parser);
            } else if (parser instanceof VisualCrossingParser) {
                parsers.put("weather-api4-data", parser);
            } else if (parser instanceof AccuWeatherParser) {
                parsers.put("weather-api5-data", parser);
            }
        }
    }

    @KafkaListener(topics = "weather-api1-data", groupId = "weather-processor-group")
    public void receiveOpenMeteoData(String messageJson) {
        processWeatherData("weather-api1-data", messageJson);
    }

    @KafkaListener(topics = "weather-api2-data")
    public void receiveTomorrowIoData(String messageJson) {
        processWeatherData("weather-api2-data", messageJson);
    }

    @KafkaListener(topics = "weather-api3-data")
    public void receiveWeatherApiData(String messageJson) {
        processWeatherData("weather-api3-data", messageJson);
    }

    @KafkaListener(topics = "weather-api4-data")
    public void receiveVissualCrossingData(String messageJson) {
        processWeatherData("weather-api4-data", messageJson);
    }

    @KafkaListener(topics = "weather-api5-data")
    public void receiveAccuWeatherData(String messageJson) {
        processWeatherData("weather-api5-data", messageJson);
    }

    private void processWeatherData(String topic, String messageJson) {
        try {
            System.out.println("Processing weather data for topic: " + topic);
            Map<String, Object> message = objectMapper.readValue(messageJson, new TypeReference<Map<String, Object>>() {});
            String username = (String) message.get("username");
            String location = (String) message.get("location");
            String jsonData = (String) message.get("weatherData");

            WeatherDataParser parser = parsers.get(topic);
            if (parser != null) {
                WeatherData weatherData = parser.parse(jsonData);
                combineWeatherData(weatherData);
                processedApis.add(topic);

                if (allApisProcessed()) {
                    weatherProcessorManager.processAllData(combinedHourlyForecasts, combinedDailyForecasts, username, location);
                    processedApis.clear();
                }

                System.out.println("Weather Data: " + weatherData.toString());
                System.out.println("HOURLY: " + getCombinedHourlyForecasts().toString());
                System.out.println("DAILY: " + getCombinedDailyForecasts().toString());
            } else {
                System.out.println("No parser found for topic: " + topic);
            }
        } catch (Exception e) {
            System.out.println("Exception occurred while processing weather data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void combineWeatherData(WeatherData weatherData) {
        for (HourlyForecast forecast : weatherData.getHourlyForecasts()) {
            LocalDateTime timestamp = forecast.getTimestamp();

            CombinedHourlyForecast combinedHourlyForecast = combinedHourlyForecasts
                    .computeIfAbsent(timestamp, k -> new CombinedHourlyForecast(timestamp));

            combinedHourlyForecast.addTemperature(forecast.getTemperature());
            combinedHourlyForecast.addHumidity(forecast.getHumidity());
            combinedHourlyForecast.addPrecipitationProbability(forecast.getPrecipitationProbability());
            combinedHourlyForecast.addWindSpeed(forecast.getWindSpeed());

            LocalDate date = timestamp.toLocalDate();
            combinedDailyForecasts.computeIfAbsent(date, k -> new CombinedDailyForecast())
                    .addForecast(forecast);
        }
    }


    public Map<LocalDate, CombinedDailyForecast> getCombinedDailyForecasts() {

        Map<LocalDate, CombinedDailyForecast> result = new TreeMap<>();

        for (Map.Entry<LocalDate, CombinedDailyForecast> entry : combinedDailyForecasts.entrySet()) {
            LocalDate date = entry.getKey();
            CombinedDailyForecast dailyForecast = entry.getValue();

            result.put(date, dailyForecast);
        }

        return result;
    }

    private boolean allApisProcessed() {
        return parsers.keySet().equals(processedApis);
    }

}




