package com.example.redissondemo;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.redisson.api.DeletedObjectListener;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec05EventListenerTest extends BaseTest {

	@Test
	public void expiredEventTest() throws InterruptedException {
		// String is the value of user:1:name key
		RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
		Mono<Void> set = bucket.set("Sam", 10, TimeUnit.SECONDS);
		Mono<Void> get = bucket.get()
				.doOnNext(System.out::println)
				.then();

		Mono<Void> event = bucket.addListener(new ExpiredObjectListener() {
			@Override
			public void onExpired(String s) {
				System.out.println(s + " key got expired from Redis.");
			}
		}).then();

		StepVerifier.create(set.concatWith(get).concatWith(event))
				.verifyComplete();

		Thread.sleep(11000);
	}

	@Test
	public void deletedEventTest() throws InterruptedException {
		// String is the value of user:1:name key
		RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
		Mono<Void> set = bucket.set("Sam", 10, TimeUnit.SECONDS);
		Mono<Void> get = bucket.get()
				.doOnNext(System.out::println)
				.then();

		Mono<Void> event = bucket.addListener(new DeletedObjectListener() {
			@Override
			public void onDeleted(String s) {
				System.out.println(s + " key got deleted from Redis.");
			}
		}).then();

		StepVerifier.create(set.concatWith(get).concatWith(event))
				.verifyComplete();

		Thread.sleep(11000);
	}
}
