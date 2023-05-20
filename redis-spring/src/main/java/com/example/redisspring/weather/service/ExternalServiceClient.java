package com.example.redisspring.weather.service;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
public class ExternalServiceClient {

	// This method updates the weather hash with latest value for every zip by calling external api
	@CachePut(value = "weather", key = "#zip")
	public int getWeatherInfo(int zip) {
		return ThreadLocalRandom.current().nextInt(20, 40); // for simulation
	}
}
