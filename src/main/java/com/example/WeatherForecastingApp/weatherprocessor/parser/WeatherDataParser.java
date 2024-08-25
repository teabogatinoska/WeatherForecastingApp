package com.example.WeatherForecastingApp.weatherprocessor.parser;

import com.example.WeatherForecastingApp.weatherprocessor.model.WeatherData;

public interface WeatherDataParser {
    WeatherData parse(String jsonData) throws Exception;
}
