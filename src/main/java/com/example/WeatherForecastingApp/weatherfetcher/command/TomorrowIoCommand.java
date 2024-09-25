package com.example.WeatherForecastingApp.weatherfetcher.command;

import com.example.WeatherForecastingApp.common.dto.UserDataRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class TomorrowIoCommand implements WeatherApiCommand{
    private static final String WEATHER_API_URL = "https://api.tomorrow.io/v4/weather/forecast?location=%s,%s&timesteps=1h&apikey=R3ERGoZFZ6ypUMDgGkC4QComRzFQdzt7";
    private static final String TOPIC = "weather-api2-data";

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
                message.put("location", requestDto.getLocation().getName());
                message.put("country", requestDto.getLocation().getCountry());
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
