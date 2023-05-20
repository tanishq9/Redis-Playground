package com.example.redisperformance.service;

import reactor.core.publisher.Mono;

public abstract class CacheTemplate<KEY, ENTITY> {

	// Cache Aside Pattern
	public Mono<ENTITY> get(KEY key) {
		return getFromCache(key)
				.switchIfEmpty(
						getFromSource(key)
								.flatMap(entity -> updateCache(key, entity))
				)
				.doOnError(System.out::println);
	}

	public Mono<ENTITY> update(KEY key, ENTITY entity) {
		return updateSource(key, entity)
				.then(updateCache(key, entity));
	}

	public Mono<Void> delete(KEY key) {
		return deleteFromSource(key)
				.then(deleteFromCache(key));
	}

	abstract protected Mono<ENTITY> getFromSource(KEY key);

	abstract protected Mono<ENTITY> getFromCache(KEY key);

	abstract protected Mono<ENTITY> updateSource(KEY key, ENTITY entity);

	abstract protected Mono<ENTITY> updateCache(KEY key, ENTITY entity);

	abstract protected Mono<Void> deleteFromSource(KEY key);

	abstract protected Mono<Void> deleteFromCache(KEY key);
}
