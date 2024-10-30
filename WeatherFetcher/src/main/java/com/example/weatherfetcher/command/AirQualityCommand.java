package com.example.weatherfetcher.command;

import com.example.common.dto.UserDataRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class AirQualityCommand implements WeatherApiCommand {

    private static final String WEATHER_API_URL = "https://air-quality-api.open-meteo.com/v1/air-quality?latitude=%s&longitude=%s&hourly=pm10,pm2_5";
    private static final String TOPIC = "weather-aq-data";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void fetchWeatherData(UserDataRequestDto requestDto, KafkaTemplate<String, String> kafkaTemplate) {

        try {
            Double latitude = requestDto.getLocation().getLatitude();
            Double longitude = requestDto.getLocation().getLongitude();
            if (latitude != null && longitude != null) {
                String apiUrl = String.format(WEATHER_API_URL, latitude.toString(), longitude.toString());
                String weatherData = fetchWeatherDataFromAPI(apiUrl);

                Map<String, Object> message = new HashMap<>();
                message.put("username", requestDto.getUsername());
                message.put("location", requestDto.getLocation());
                message.put("weatherData", weatherData);

                String messageJson = objectMapper.writeValueAsString(message);
                kafkaTemplate.send(TOPIC, messageJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fetchWeatherDataFromAPI(String apiUrl) {
        return restTemplate.getForObject(apiUrl, String.class);
    }

}
