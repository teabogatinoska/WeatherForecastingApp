package com.example.WeatherForecastingApp.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final long CACHE_TTL = 1;

    public void cacheData(String key,  ArrayList<String> data) {
        redisTemplate.opsForValue().set(key, data, CACHE_TTL, TimeUnit.HOURS);
    }

    public ArrayList<String> getCachedData(String key) {
        return (ArrayList<String>) redisTemplate.opsForValue().get(key);
    }
}
