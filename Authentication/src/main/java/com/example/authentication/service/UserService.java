package com.example.authentication.service;

import com.example.authentication.model.Location;
import com.example.authentication.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    User addFavoriteLocation(Long userId, String location, String country, Double latitude, Double longitude);
    User removeFavoriteLocation(Long userId, Long locationId);
    Long addRecentSearch(Long userId, String location, String country, Double latitude, Double longitude);
    List<Location> getFavoriteLocations(Long userId);
    List<Location> getRecentSearches(Long userId);

    Map<Long, List<Location>> getAllUsersFavoriteLocations();
}
