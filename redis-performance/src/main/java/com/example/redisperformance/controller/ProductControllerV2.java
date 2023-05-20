package com.example.redisperformance.controller;

import com.example.redisperformance.entity.Product;
import com.example.redisperformance.service.ProductServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("product/v2")
public class ProductControllerV2 {

	@Autowired
	private ProductServiceV2 productServiceV2;

	@GetMapping("{id}")
	Mono<Product> getProduct(@PathVariable int id) {
		return productServiceV2.getProduct(id);
	}

	@PutMapping("{id}")
	Mono<Product> putProduct(@PathVariable int id, @RequestBody Mono<Product> productMono) {
		return productServiceV2.updateProduct(id, productMono);
	}

	@DeleteMapping("{id}")
	Mono<Void> putProduct(@PathVariable int id) {
		return productServiceV2.deleteProduct(id);
	}
}
