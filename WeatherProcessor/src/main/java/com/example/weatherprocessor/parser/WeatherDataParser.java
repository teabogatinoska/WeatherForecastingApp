package com.example.weatherprocessor.parser;

import com.example.weatherprocessor.model.WeatherData;

public interface WeatherDataParser {
    WeatherData parse(String jsonData) throws Exception;
}
