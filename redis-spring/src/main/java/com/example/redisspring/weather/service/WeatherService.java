package com.example.redisspring.weather.service;

import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

	@Autowired
	ExternalServiceClient externalServiceClient;

	// get weather for a zip, only what is stored in Redis, this method would never be executed (if zipCode is b/w [1,5])
	@Cacheable("weather")
	public int getInfo(int zip) {
		System.out.println("Value not in cache for: " + zip);
		return 0;
	}

	// Every 10 seconds, update the weather for [1,5] zipCodes by calling external api
	@Scheduled(fixedRate = 10_000)
	public void update() {
		System.out.println("updating weather");
		IntStream.rangeClosed(1, 5)
				.forEach(externalServiceClient::getWeatherInfo);
	}
}
