package com.example.redisperformance.service;

import com.example.redisperformance.entity.Product;
import com.example.redisperformance.repository.ProductRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductCacheTemplate extends CacheTemplate<Integer, Product> {

	@Autowired
	private ProductRepository productRepository;


	private final RedissonReactiveClient redissonReactiveClient;
	private final RMapReactive<Integer, Product> map;
	private final RScoredSortedSetReactive<Integer> scoredSortedSet;

	public ProductCacheTemplate(RedissonReactiveClient redissonReactiveClient) {
		this.redissonReactiveClient = redissonReactiveClient;
		this.map = redissonReactiveClient.getMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class));
		scoredSortedSet = redissonReactiveClient.getScoredSortedSet("product:visit", new TypedJsonJacksonCodec(Integer.class));
	}

	@Override
	protected Mono<Product> getFromSource(Integer id) {
		return this.productRepository.findById(id)
				.flatMap(product -> scoredSortedSet
						.addScore(product.getId(), 1)
						.thenReturn(product)); // Update sorted set and return same product
	}

	@Override
	protected Mono<Product> getFromCache(Integer id) {
		return this.map.get(id)
				.flatMap(product -> scoredSortedSet
						.addScore(product.getId(), 1)
						.thenReturn(product)); // Update sorted set and return same product
	}

	@Override
	protected Mono<Product> updateSource(Integer id, Product product) {
		return this.productRepository.findById(id)
				.doOnNext(p -> product.setId(id))
				.flatMap(p -> this.productRepository.save(product));
	}

	@Override
	protected Mono<Product> updateCache(Integer id, Product product) {
		return this.map.fastPut(id, product).thenReturn(product);
	}

	@Override
	protected Mono<Void> deleteFromSource(Integer id) {
		return this.productRepository.deleteById(id);
	}

	@Override
	protected Mono<Void> deleteFromCache(Integer id) {
		return this.map.fastRemove(id).then();
	}

	@Scheduled(fixedRate = 10_000)
	void top3Products() {
		System.out.println("Getting top 3 products");

		RBatchReactive batch = this.redissonReactiveClient.createBatch(BatchOptions.defaults());
		String format = DateTimeFormatter.ofPattern("YYYYMMDD").format(LocalDate.now());

		// Publish top 3 product entries to Redis in a Batch as another scored set
		RScoredSortedSetReactive<Integer> top3Set = batch.getScoredSortedSet("product:visit:top3" + format, IntegerCodec.INSTANCE);

		this.scoredSortedSet
				// Getting the top 3 products
				.entryRangeReversed(0, 2)
				.doOnNext(
						scoredEntries -> scoredEntries.forEach(
								integerScoredEntry -> {
									// scoredSet.add will override, addScore() will add to existing
									top3Set.add(integerScoredEntry.getScore(), integerScoredEntry.getValue()).subscribe();

									// Printing top 3 products
									System.out.println("Product Id: " + integerScoredEntry.getValue() + ", with score: " + integerScoredEntry.getScore());
								}
						)
				)
				.then(batch.execute())
				.subscribe(); // Consuming all operations of this reactive pipeline
		// batch.execute().subscribe(System.out::println, System.out::println);
	}
}
