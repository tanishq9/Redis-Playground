package com.example.redissondemo;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLongReactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec03NumberTest extends BaseTest {

	@Test
	void incrementTest() {
		// set k v --> incr, decr
		RAtomicLongReactive atomicLong = this.client.getAtomicLong("user:1:visit");
		atomicLong.set(2);
		Mono<Void> mono = Flux.range(1, 30)
				.delayElements(Duration.ofMillis(1))
				.flatMap(integer -> atomicLong.incrementAndGet())
				.then();
		StepVerifier.create(mono)
				.verifyComplete();
	}
}
