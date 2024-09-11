package com.example.WeatherForecastingApp.authentication.repository;

import com.example.WeatherForecastingApp.authentication.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByNameAndCountry(String name, String country);
}
