package com.example.WeatherForecastingApp.authentication.service;

import com.example.WeatherForecastingApp.authentication.model.Location;
import com.example.WeatherForecastingApp.authentication.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    User addFavoriteLocation(Long userId, String location, String country, double latitude, double longitude);
    User removeFavoriteLocation(Long userId, Long locationId);
    User addRecentSearch(Long userId, String location, String country, double latitude, double longitude);
    List<Location> getFavoriteLocations(Long userId);
    List<Location> getRecentSearches(Long userId);

    Map<Long, List<Location>> getAllUsersFavoriteLocations();
}
