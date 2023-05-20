package com.example.redisperformance.service;

import com.example.redisperformance.entity.Product;
import com.example.redisperformance.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceV1 {

	@Autowired
	private ProductRepository productRepository;

	public Mono<Product> getProduct(int id) {
		return this.productRepository.findById(id);
	}

	public Mono<Product> updateProduct(int id, Mono<Product> product) {
		return this.productRepository.findById(id)
				.flatMap(
						p -> product.doOnNext(pr -> pr.setId(p.getId()))
				)
				.flatMap(productRepository::save);
	}
}
