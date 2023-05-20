package com.example.redisperformance.controller;

import com.example.redisperformance.entity.Product;
import com.example.redisperformance.service.ProductServiceV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("product/v1")
public class ProductControllerV1 {

	@Autowired
	private ProductServiceV1 productServiceV1;

	@GetMapping("{id}")
	Mono<Product> getProduct(@PathVariable int id) {
		return productServiceV1.getProduct(id);
	}

	@PutMapping("{id}")
	Mono<Product> putProduct(@PathVariable int id, @RequestBody Mono<Product> productMono) {
		return productServiceV1.updateProduct(id, productMono);
	}
}
