package com.example.redissondemo;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec01KeyValueTest extends BaseTest {

	@Test
	public void keyValueAccessTest() {
		// RBucketReactive<String>, String is the value for redis key
		RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
		Mono<Void> set = bucket.set("Tanishq");
		// https://github.com/tanishq9/Reactive-Microservices-with-Spring-WebFlux/blob/74ad3757d91620256398dcb0e0eece33b3e85d25/product-service/src/main/java/com/example/productservice/service/ProductService.java#L54
		Mono<Void> get = bucket.get()
				.doOnNext(System.out::println)
				.then(); // to return Mono<Void> on completion -- https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#then--

		StepVerifier.create(set.concatWith(get))
				.verifyComplete();

		// For quick demonstration/revision
		Flux<Integer> flux = Flux.just(1, 2, 3, 4)
				.doOnNext(val -> System.out.println("Sending " + val));

		flux.subscribe(val -> System.out.println("Receiving " + val));

		StepVerifier.create(flux)
				.expectNext(1, 2, 3, 4)
				.verifyComplete();
	}

	@Test
	public void keyValueAccessTestWithExpiry() {
		RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
		Mono<Void> set = bucket.set("Sam", 60, TimeUnit.SECONDS);
		// https://github.com/tanishq9/Reactive-Microservices-with-Spring-WebFlux/blob/74ad3757d91620256398dcb0e0eece33b3e85d25/product-service/src/main/java/com/example/productservice/service/ProductService.java#L54
		Mono<Void> get = bucket.get()
				.doOnNext(System.out::println)
				.then(); // to return Mono<Void> on completion -- https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#then--

		StepVerifier.create(set.concatWith(get))
				.verifyComplete();
	}

	@Test
	public void keyValueAccessTestExtendExpiry() throws InterruptedException {
		RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
		Mono<Void> set = bucket.set("Sam", 10, TimeUnit.SECONDS);
		// https://github.com/tanishq9/Reactive-Microservices-with-Spring-WebFlux/blob/74ad3757d91620256398dcb0e0eece33b3e85d25/product-service/src/main/java/com/example/productservice/service/ProductService.java#L54
		Mono<Void> get = bucket.get()
				.doOnNext(System.out::println)
				.then(); // to return Mono<Void> on completion -- https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#then--

		StepVerifier.create(set.concatWith(get))
				.verifyComplete();

		// extend
		Thread.sleep(5000);
		Mono<Boolean> expire = bucket.expire(60, TimeUnit.SECONDS);

		StepVerifier.create(expire)
				.expectNext(true)
				.verifyComplete();

		// access bucket expiration time
		// either get value of a publisher by subscribing it
		bucket.remainTimeToLive().subscribe(System.out::println);
		// or test using step verifier
		StepVerifier.create(bucket.remainTimeToLive().doOnNext(System.out::println))
				.expectNextCount(1)
				.verifyComplete();
		// other way to test it using StepVerifier but not expectNext
		Mono<Void> longMono = bucket.remainTimeToLive()
				.doOnNext(System.out::println)
				.then();
		StepVerifier.create(longMono)
				.verifyComplete();
	}
}
