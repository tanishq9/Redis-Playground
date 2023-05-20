package com.example.redisperformance.service;

import com.example.redisperformance.entity.Product;
import com.example.redisperformance.repository.ProductRepository;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DataSetupService implements CommandLineRunner {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private R2dbcEntityTemplate entityTemplate;

	@Value("classpath:schema.sql")
	private Resource resource;

	@Override
	public void run(String... args) throws Exception {
		String query = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
		System.out.println(query);

		Mono<Void> insert = Flux.range(1, 1000)
				.map(i -> new Product(null, "product" + i, 10))
				.collectList()
				.flatMapMany(productRepository::saveAll)
				.then();

		this.entityTemplate.getDatabaseClient()
				.sql(query)
				.then()
				.then(insert)
				.doFinally(s -> System.out.println("Data setup done " + s))
				.subscribe();
	}
}
