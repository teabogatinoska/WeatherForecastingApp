package com.example.WeatherForecastingApp.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserDataRequestDto {

    private Long userId;
    private String username;
    private LocationDto location;

    public UserDataRequestDto() {}

    @Override
    public String toString() {
        return "UserDataRequestDto{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", location=" + location +
                '}';
    }
}
