package com.example.redisspring.fib.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FibService {

	// have a strategy to cache evict
	@Cacheable(value = "math:fib", key = "#number")
	// a hash type key would be created, hash would contain values corresponding to different input values of this method
	public Mono<Integer> computeFib(int number, String name) {
		System.out.println("Computing Fib for: " + number);
		return Mono.just(computeFibHelper(number));
	}

	// Put, Post, Patch, Delete corresponding methods in service layer would be having this @CacheEvict annotation
	// For example, if entry is updated then we have to update cache/Redis as well
	@CacheEvict(value = "math:fib", key = "#number")
	public void clearCache(int number) {
		System.out.println("Clearing hash key: " + number);
	}

	// Scheduled evict - for example every 10 seconds do cache evict
	@Scheduled(fixedRate = 10_000)
	@CacheEvict(value = "math:fib", allEntries = true)
	public void clearCache() {
		System.out.println("Clearing all entries");
	}

	// O(2^N) Intentionally done to simulate time taking computation
	private int computeFibHelper(int number) {
		if (number < 2) {
			return number;
		}
		return computeFibHelper(number - 1) + computeFibHelper(number - 2);
	}
}
