package com.example.WeatherForecastingApp.common.dto;

import com.example.WeatherForecastingApp.authentication.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFavoriteLocationEvent {
    private Long userId;
    private List<LocationDto> locations;
}
