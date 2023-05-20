package com.example.redisspring.city.service;

import com.example.redisspring.city.client.CityClient;
import com.example.redisspring.city.dto.City;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.redisson.api.RMapCacheReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CityService {

	@Autowired
	private CityClient cityClient;

	private final RMapReactive<String, City> cityMap;

	// private final RMapCacheReactive<String, City> cityCacheMap; // provides option to set ttl for map keys

	public CityService(RedissonReactiveClient redissonReactiveClient) {
		this.cityMap = redissonReactiveClient.getMap("city", new TypedJsonJacksonCodec(String.class, City.class));
		// this.cityCacheMap = redissonReactiveClient.getMapCache("city", new TypedJsonJacksonCodec(String.class, City.class));
	}

	/*
	 * get from cache
	 * if empty then get from db/source and put in cache
	 * return
	 * */
	public Mono<City> getCity(final String zipCode) {
		return cityMap.get(zipCode)
				.switchIfEmpty(
						cityClient.getCity(zipCode)
								.flatMap(city -> this.cityMap.fastPut(zipCode, city).thenReturn(city))
				);
	}

	// Its not optimal to make call to external service everytime hence we update the cache every 60 seconds
	// @Scheduled(fixedRate = 30_000)
	public void refreshCity() {
		System.out.println("--- Starting Update ---");
		IntStream.rangeClosed(10000, 10009)
				.forEach(
						zipCode -> cityClient
								.getCity(String.valueOf(zipCode)) // get the recent value
								.doOnNext(city -> System.out.println("zipCode: " + zipCode + ", city: " + city.toString()))
								.flatMap(city -> this.cityMap.fastPut(String.valueOf(zipCode), city)) // update the cache
								.subscribe() // consuming the reactive stream to update
				);
	}

	// Its not optimal to make call to external service everytime hence we update the cache every 60 seconds
	// @Scheduled(fixedRate = 30_000)
	public void refreshCityAlternative() {
		this.cityClient.getAll()
				.collectList()
				.map(list -> list.stream().collect(Collectors.toMap(City::getZip, Function.identity())))
				.flatMap(cityMap::putAll)
				.subscribe();
	}
}
