package com.example.WeatherForecastingApp.authentication.service.impl;

import com.example.WeatherForecastingApp.authentication.model.Location;
import com.example.WeatherForecastingApp.authentication.model.User;
import com.example.WeatherForecastingApp.authentication.repository.LocationRepository;
import com.example.WeatherForecastingApp.authentication.repository.UserRepository;
import com.example.WeatherForecastingApp.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;

    public UserServiceImpl(UserRepository userRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public User addFavoriteLocation(Long userId, String location, String country, double latitude, double longitude) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Location favouriteLocation = locationRepository.findByNameAndCountry(location, country)
                .orElseGet(() -> locationRepository.save(new Location(location, latitude, longitude, country)));

        user.addFavoriteCity(favouriteLocation);
        return userRepository.save(user);
    }

    @Override
    public User removeFavoriteLocation(Long userId, Long locationId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Location location = locationRepository.findById(locationId).orElseThrow(() -> new RuntimeException("Location not found"));

        user.removeFavoriteCity(location);
        return userRepository.save(user);
    }

    @Override
    public User addRecentSearch(Long userId, String location, String country, double latitude, double longitude) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Location recentLocation = locationRepository.findByNameAndCountry(location, country)
                .orElseGet(() -> locationRepository.save(new Location(location, latitude, longitude, country)));

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
}
