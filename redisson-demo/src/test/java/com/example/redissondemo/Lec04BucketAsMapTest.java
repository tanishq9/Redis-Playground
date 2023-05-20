package com.example.redissondemo;

import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec04BucketAsMapTest extends BaseTest {
	// user:1:name
	// user:2:name
	// user:3:name
	@Test
	public void bucketAsMap() {
		Mono<Void> mono = this.client.getBuckets(StringCodec.INSTANCE)
				.get("user:1:name", "user:2:name", "user:3:name") // returns Mono<Map<String, String>>
				.doOnNext(stringObjectMap -> System.out.println(stringObjectMap))
				.then();

		StepVerifier.create(mono)
				.verifyComplete();
	}
}
