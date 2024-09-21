package com.example.WeatherForecastingApp.common;

import com.example.WeatherForecastingApp.common.dto.EventRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;




@Component
public class EventStoreUtils {

    @Autowired
    @LoadBalanced
    private final RestTemplate restTemplate;

    private final String eventStoreServiceUrl = "http://event-store";

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
