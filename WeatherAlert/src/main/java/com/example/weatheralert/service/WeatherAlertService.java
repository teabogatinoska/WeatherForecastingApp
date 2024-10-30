package com.example.weatheralert.service;

import com.example.common.EventStoreUtils;
import com.example.common.dto.UserFavoriteLocationEvent;
import com.example.weatheralert.model.Location;
import com.example.weatheralert.model.LocationAlerts;
import com.example.weatheralert.model.WeatherAlert;
import com.example.weatheralert.model.WeatherAlertEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class WeatherAlertService implements ApplicationRunner {
    private final Map<Long, List<Location>> userFavoriteLocations = new ConcurrentHashMap<>();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private final EventStoreUtils eventStoreUtils;

    @Value("${authentication.service.url}")
    private String authenticationServiceUrl;

    @Value("${weather.alerts.api.url}")
    private String weatherAlertsApiUrl;

    @Autowired
    @Qualifier("externalRestTemplate")
    private RestTemplate externalRestTemplate;


    public WeatherAlertService(EventStoreUtils eventStoreUtils) {
        this.eventStoreUtils = eventStoreUtils;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeUserFavoriteLocations();
    }

    //@Scheduled(fixedDelay = 10000, initialDelay = 15000)
    // @PostConstruct
    public void initializeUserFavoriteLocations() {
        System.out.println("Initializing favorite locations from the database...");

        Map<Long, List<Location>> allFavorites = fetchAllUsersFavoriteLocations();

        if (allFavorites != null && !allFavorites.isEmpty()) {
            userFavoriteLocations.putAll(allFavorites);
            System.out.println("Favorite locations initialized: " + userFavoriteLocations);
        } else {
            System.out.println("No favorite locations found during initialization.");
        }
    }

    private Map<Long, List<Location>> fetchAllUsersFavoriteLocations() {
        String url = authenticationServiceUrl + "/user/users-favorites";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map<Long, List<Location>>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, new ParameterizedTypeReference<Map<Long, List<Location>>>() {
                });

        return response.getBody();
    }


    private List<Location> fetchFavoriteLocations(Long userId) {
        String url = authenticationServiceUrl + "/user/" + userId + "/favorites";
        System.out.println("Calling authentication service at URL: " + url);
        ResponseEntity<List<Location>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Location>>() {
                });
        return response.getBody();
    }

    @KafkaListener(topics = "user-favorite-locations-deleted", groupId = "weather-alert-group")
    public void handleFavoriteLocationsDelete(UserFavoriteLocationEvent event) {
        try {
            System.out.println("New location added: " + event);

            String eventJson = objectMapper.writeValueAsString(event);
            eventStoreUtils.writeEventToEventStore("user-favorite-location-deleted", "UserFavoriteLocationDeleted", eventJson);

            List<Location> newLocations = event.getLocations().stream()
                    .map(dto -> new Location(dto.getName(), dto.getCountry()))
                    .collect(Collectors.toList());

            System.out.println("New locations updated: " + newLocations);
            userFavoriteLocations.put(event.getUserId(), newLocations);

            fetchAndSendWeatherAlerts(event.getUserId(), newLocations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "user-favorite-locations-updated", groupId = "weather-alert-group")
    public void handleFavoriteLocationsUpdate(UserFavoriteLocationEvent event) {
        try {
            System.out.println("New location added: " + event);

            String eventJson = objectMapper.writeValueAsString(event);
            eventStoreUtils.writeEventToEventStore("user-favorite-location-updated", "UserFavoriteLocationUpdated", eventJson);

            List<Location> newLocations = event.getLocations().stream()
                    .map(dto -> new Location(dto.getName(), dto.getCountry()))
                    .collect(Collectors.toList());

            System.out.println("New locations updated: " + newLocations);
            userFavoriteLocations.put(event.getUserId(), newLocations);

            fetchAndSendWeatherAlerts(event.getUserId(), newLocations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


     @Scheduled(fixedRate = 60000, initialDelay = 18000)
    //@Scheduled(cron = "0 0 8,20 * * *")
    public void fetchWeatherAlerts() {
        System.out.println("Fetching Alerts");

        if (userFavoriteLocations.isEmpty()) {
            System.out.println("No users with favorite locations found.");
        }

        userFavoriteLocations.forEach((userId, locations) -> {
            System.out.println("User ID: " + userId + ", Locations: " + locations);
            if (locations != null && !locations.isEmpty()) {
                fetchAndSendWeatherAlerts(userId, locations);
            } else {
                List<Location> fetchedLocations = fetchFavoriteLocations(userId);
                if (fetchedLocations != null && !fetchedLocations.isEmpty()) {
                    System.out.println("Fetched locations: " + fetchedLocations);
                    userFavoriteLocations.put(userId, fetchedLocations);
                    fetchAndSendWeatherAlerts(userId, fetchedLocations);
                }
            }
        });
    }


    private void fetchAndSendWeatherAlerts(Long userId, List<Location> locations) {
        List<Map<String, Object>> locationAlertsList = new ArrayList<>();

        locations.forEach(location -> {
            String apiUrl = String.format(weatherAlertsApiUrl, location.getName());
            String response = externalRestTemplate.getForObject(apiUrl, String.class);
            System.out.println("Calling API!!");
            List<WeatherAlert> alerts = parseWeatherAlerts(response);

            if (!alerts.isEmpty()) {
                System.out.println("Adding alerts for location: " + location.getName());

                Map<String, Object> locationAlertsMap = new HashMap<>();
                locationAlertsMap.put("location", location);
                locationAlertsMap.put("alerts", alerts);

                locationAlertsList.add(locationAlertsMap);
            }
        });

        Map<String, Object> alertEventMap = new HashMap<>();
        alertEventMap.put("userId", userId);
        alertEventMap.put("locationAlerts", locationAlertsList);

        kafkaTemplate.send("user-weather-alerts", alertEventMap);

    }


    private List<WeatherAlert> parseWeatherAlerts(String response) {
        List<WeatherAlert> weatherAlerts = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode alertsNode = root.path("alerts").path("alert");

            if (alertsNode.isArray()) {
                for (JsonNode alertNode : alertsNode) {
                    String headline = alertNode.path("headline").asText();
                    String severity = alertNode.path("severity").asText();
                    String urgency = alertNode.path("urgency").asText();
                    String areas = alertNode.path("areas").asText();
                    String event = alertNode.path("event").asText();
                    String effective = alertNode.path("effective").asText();
                    String expires = alertNode.path("expires").asText();

                    WeatherAlert weatherAlert = new WeatherAlert(headline, severity, urgency, areas, event, effective, expires);
                    weatherAlerts.add(weatherAlert);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherAlerts;
    }


}
