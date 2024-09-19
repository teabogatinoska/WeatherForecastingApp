package com.example.WeatherForecastingApp.common;

import com.example.WeatherForecastingApp.common.dto.EventRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;




@Component
public class EventStoreUtils {

    private final RestTemplate restTemplate;
    private final String eventStoreServiceUrl = "http://localhost:8081";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EventStoreUtils(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

    }

    public void writeEventToEventStore(String streamName, String eventType, String eventData) {
        System.out.println("INSIDE COMMON - Writing event to Event Store");
        String url = eventStoreServiceUrl + "/api/events/write/" + streamName;

        EventRequest eventRequest = new EventRequest(streamName, eventType, eventData);
        System.out.println("EVENT REQUEST: " + eventRequest.toString());
        try {
            System.out.println("Sending POST request to: " + url);
            restTemplate.postForEntity(url, eventRequest, String.class);
            System.out.println("Event written successfully");
        } catch (Exception e) {
            System.err.println("Failed to write event: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
