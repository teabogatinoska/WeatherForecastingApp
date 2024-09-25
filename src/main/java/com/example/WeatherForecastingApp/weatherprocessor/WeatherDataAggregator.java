package com.example.WeatherForecastingApp.weatherprocessor;

import com.example.WeatherForecastingApp.common.EventStoreUtils;
import com.example.WeatherForecastingApp.weatherprocessor.model.*;
import com.example.WeatherForecastingApp.weatherprocessor.parser.*;
import com.example.WeatherForecastingApp.weatherprocessor.processor.WeatherProcessorManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    private final Map<LocalDateTime, AirQualityData> airQualityDataMap = new TreeMap<>();

    private final int TOTAL_APIS = 5;
    private int apiCallCount = 0;
    private int successfulApis = 0;
    private Timer apiResponseTimer;
    private boolean timerStarted = false;
    private final long TIMEOUT_DURATION = 5000;

    @Autowired
    private final EventStoreUtils eventStoreUtils;

    @Autowired
    public WeatherDataAggregator(List<WeatherDataParser> parserList, WeatherProcessorManager weatherProcessorManager, EventStoreUtils eventStoreUtils) {
        this.eventStoreUtils = eventStoreUtils;
        this.parsers = new HashMap<>();
        this.combinedHourlyForecasts = new TreeMap<>();
        this.combinedDailyForecasts = new TreeMap<>();
        this.weatherProcessorManager = weatherProcessorManager;
        this.processedApis = new HashSet<>();
        System.out.println("INSIDE CONSTRUCTOR");
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
        eventStoreUtils.writeEventToEventStore("weather-fetched-data", "FetchedWeatherData", messageJson);
        processWeatherData("weather-api1-data", messageJson);
    }

    @KafkaListener(topics = "weather-api2-data")
    public void receiveTomorrowIoData(String messageJson) {

        eventStoreUtils.writeEventToEventStore("weather-fetched-data", "FetchedWeatherData", messageJson);
        processWeatherData("weather-api2-data", messageJson);
    }

    @KafkaListener(topics = "weather-api3-data")
    public void receiveWeatherApiData(String messageJson) {

        eventStoreUtils.writeEventToEventStore("weather-fetched-data", "FetchedWeatherData", messageJson);
        processWeatherData("weather-api3-data", messageJson);
    }

    @KafkaListener(topics = "weather-api4-data")
    public void receiveVissualCrossingData(String messageJson) {
        eventStoreUtils.writeEventToEventStore("weather-fetched-data", "FetchedWeatherData", messageJson);
        processWeatherData("weather-api4-data", messageJson);
    }

    @KafkaListener(topics = "weather-api5-data")
    public void receiveAccuWeatherData(String messageJson) {
        eventStoreUtils.writeEventToEventStore("weather-fetched-data", "FetchedWeatherData", messageJson);
        processWeatherData("weather-api5-data", messageJson);
    }

    @KafkaListener(topics = "weather-aq-data", groupId = "weather-processor-group")
    public void receiveAirQualityData(String messageJson) {
        try {
            Map<String, Object> message = objectMapper.readValue(messageJson, new TypeReference<Map<String, Object>>() {
            });
            String airQualityData = (String) message.get("weatherData");
            processAirQualityData(airQualityData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void processWeatherData(String topic, String messageJson) {
        try {
            System.out.println("Processing weather data for topic: " + topic);
            Map<String, Object> message = objectMapper.readValue(messageJson, new TypeReference<Map<String, Object>>() {
            });
            String username = (String) message.get("username");
            String location = (String) message.get("location");
            String country = (String) message.get("country");
            String jsonData = (String) message.get("weatherData");

            if (!timerStarted) {
                startApiResponseTimer(username, location, country);
                timerStarted = true;
            }

            WeatherDataParser parser = parsers.get(topic);
            System.out.println("TOPIC: " + topic);
            apiCallCount++;
            if (parser != null) {
                WeatherData weatherData = parser.parse(jsonData);
                combineWeatherData(weatherData);
                successfulApis++;

                if (apiCallCount == TOTAL_APIS) {
                    processAllData(username, location, country);
                }

                //System.out.println("HOURLY: " + getCombinedHourlyForecasts().toString());
                //System.out.println("DAILY: " + getCombinedDailyForecasts().toString());
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
            System.out.println("Forecast description: " + forecast.getDescription());
            combinedHourlyForecast.setDescription(forecast.getDescription());


            LocalDate date = timestamp.toLocalDate();
            combinedDailyForecasts.computeIfAbsent(date, k -> new CombinedDailyForecast())
                    .addForecast(forecast);
        }
    }


    private void processAirQualityData(String airQualityData) {
        try {
            JsonNode rootNode = objectMapper.readTree(airQualityData);
            JsonNode hourlyPm10 = rootNode.path("hourly").path("pm10");
            JsonNode hourlyPm25 = rootNode.path("hourly").path("pm2_5");
            JsonNode times = rootNode.path("hourly").path("time");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]");
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

            for (int i = 0; i < times.size(); i++) {
                LocalDateTime timestamp = LocalDateTime.parse(times.get(i).asText(), formatter);
                if (!timestamp.isBefore(now)) {
                    double pm10 = hourlyPm10.get(i).asDouble();
                    double pm25 = hourlyPm25.get(i).asDouble();

                    airQualityDataMap.put(timestamp, new AirQualityData(pm10, pm25));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startApiResponseTimer(String username, String location, String country) {
        apiResponseTimer = new Timer();
        apiResponseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
               System.out.println("Timeout reached. Processing available data.");
                processAllData(username, location, country);
            }
        }, TIMEOUT_DURATION);
    }

    private void processAllData(String username, String location, String country) {
        if (successfulApis >= 5) {
            System.out.println("All 5 APIs responded successfully. Processing data.");
        } else if (successfulApis >= 2) {
            System.out.println("Only " + successfulApis + " APIs responded successfully. Proceeding with available data.");
        } else {
            System.out.println("Not enough APIs returned valid data. Cannot proceed.");

            return;
        }

        weatherProcessorManager.processAllData(combinedHourlyForecasts, combinedDailyForecasts, airQualityDataMap, username, location, country);

        resetApiState();
    }

    private void resetApiState() {
        apiCallCount = 0;
        successfulApis = 0;
        timerStarted = false;
        if (apiResponseTimer != null) {
            apiResponseTimer.cancel();
        }
    }


}





