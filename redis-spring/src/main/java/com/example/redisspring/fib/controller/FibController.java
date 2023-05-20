package com.example.redisspring.fib.controller;

import com.example.redisspring.fib.service.FibService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("fib")
public class FibController {

	@Autowired
	FibService service;

	@GetMapping("{input}/{name}")
	public Mono<Integer> getFib(@PathVariable int input, @PathVariable String name) {
		return service.computeFib(input, name);
	}
}
