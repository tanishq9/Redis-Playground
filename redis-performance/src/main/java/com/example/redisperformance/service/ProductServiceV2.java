package com.example.redisperformance.service;

import com.example.redisperformance.entity.Product;
import com.example.redisperformance.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceV2 {

	@Autowired
	private CacheTemplate<Integer, Product> cacheTemplate;

	public Mono<Product> getProduct(int id) {
		return cacheTemplate.get(id);
	}

	public Mono<Product> updateProduct(int id, Mono<Product> product) {
		// using flatmap so as to consume/subscribe to the result of update
		return product
				.doOnNext(pr -> pr.setId(id))
				.flatMap(pr -> cacheTemplate.update(id, pr));
	}

	public Mono<Void> deleteProduct(int id) {
		return cacheTemplate.delete(id);
	}
}
