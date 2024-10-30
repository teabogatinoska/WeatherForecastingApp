package com.example.weatherfetcher.command;

import com.example.common.dto.UserDataRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.node.ObjectNode;
@Component
public class VisualCrossingCommand implements WeatherApiCommand{
    private static final String WEATHER_API_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/%s,%s/next7days?unitGroup=metric&include=hours&key=WY2C5X7BA532TDR3PN8GXNPTW&contentType=json";
    private static final String TOPIC = "weather-api4-data";

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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String fetchWeatherDataFromAPI(String apiUrl) {
        return restTemplate.getForObject(apiUrl, String.class);
    }
}
