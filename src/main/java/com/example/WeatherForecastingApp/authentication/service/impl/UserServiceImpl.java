package com.example.WeatherForecastingApp.authentication.service.impl;

import com.example.WeatherForecastingApp.authentication.exception.LocationAlreadyFavoriteException;
import com.example.WeatherForecastingApp.common.dto.LocationDto;
import com.example.WeatherForecastingApp.common.dto.UserFavoriteLocationEvent;
import com.example.WeatherForecastingApp.authentication.model.Location;
import com.example.WeatherForecastingApp.authentication.model.User;
import com.example.WeatherForecastingApp.authentication.repository.LocationRepository;
import com.example.WeatherForecastingApp.authentication.repository.UserRepository;
import com.example.WeatherForecastingApp.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private KafkaTemplate<String, UserFavoriteLocationEvent> kafkaTemplate;

    public UserServiceImpl(UserRepository userRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public User addFavoriteLocation(Long userId, String location, String country, Double latitude, Double longitude) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Location favoriteLocation = locationRepository.findByNameAndCountry(location, country)
                .orElseGet(() -> locationRepository.save(new Location(location, country, latitude, longitude)));

        if (user.getFavoriteCities() == null) {
            user.setFavoriteCities(new HashSet<>());
        }

        if (!user.getFavoriteCities().contains(favoriteLocation)) {
            user.addFavoriteCity(favoriteLocation);
            User updatedUser = userRepository.save(user);

            List<LocationDto> locationDtos = updatedUser.getFavoriteCities().stream()
                    .map(loc -> new LocationDto(loc.getName(), loc.getCountry(), loc.getLatitude(), loc.getLongitude()))
                    .collect(Collectors.toList());

            UserFavoriteLocationEvent event = new UserFavoriteLocationEvent();
            event.setUserId(userId);
            event.setLocations(locationDtos);

            kafkaTemplate.send("user-favorite-locations-updated", event);

            return updatedUser;
        } else {
            throw new LocationAlreadyFavoriteException("Location is already marked as favorite.");

        }
    }


    @Override
    public User removeFavoriteLocation(Long userId, Long locationId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new RuntimeException("Location not found"));

        user.removeFavoriteCity(location);
        User updatedUser = userRepository.save(user);

        List<LocationDto> locationDtos = updatedUser.getFavoriteCities().stream()
                .map(loc -> new LocationDto(loc.getName(), loc.getCountry(), loc.getLatitude(), loc.getLongitude()))
                .collect(Collectors.toList());

        UserFavoriteLocationEvent event = new UserFavoriteLocationEvent();
        event.setUserId(userId);
        event.setLocations(locationDtos);

        kafkaTemplate.send("user-favorite-locations-deleted", event);

        return updatedUser;
    }

    @Override
    public User addRecentSearch(Long userId, String location, String country, Double latitude, Double longitude) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("Inside auth service: " + location + " " + country);
        String normalizedLocation = location.trim().replaceAll("\\s+", " ");
        String normalizedCountry = country.trim();

        System.out.println("Location and country auth service: " + normalizedLocation + " " + normalizedCountry);

        Location recentLocation = locationRepository.findByNameAndCountry(normalizedLocation, normalizedCountry)
                .orElseGet(() -> locationRepository.save(new Location(normalizedLocation, normalizedCountry, latitude, longitude)));

        user.addRecentSearch(recentLocation);
        return userRepository.save(user);
    }

    @Override
    public List<Location> getFavoriteLocations(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return new ArrayList<>(user.getFavoriteCities());
    }

    @Override
    public List<Location> getRecentSearches(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRecentSearches();
    }

    @Override
    public Map<Long, List<Location>> getAllUsersFavoriteLocations() {
        List<Object[]> usersWithLocations = userRepository.findAllUsersWithFavoriteLocations();

        Map<Long, List<Location>> userFavoritesMap = new HashMap<>();

        for (Object[] result : usersWithLocations) {
            Long userId = (Long) result[0];
            Location location = (Location) result[1];

            userFavoritesMap.putIfAbsent(userId, new ArrayList<>());
            userFavoritesMap.get(userId).add(location);
        }

        return userFavoritesMap;
    }
}
