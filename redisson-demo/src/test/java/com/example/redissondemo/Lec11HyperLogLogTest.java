package com.example.redissondemo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.redisson.api.RHyperLogLogReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec11HyperLogLogTest extends BaseTest {

/*
	HyperLogLog is a probabilistic data structure which doesn't store the item but can be used to give the unique count,
	and therefore can be used in-case we want a high level estimation of unique count regarding something like website visits WITHOUT storing the websites,
	this is a less size consuming solution compared to set or list for the same use-case.
*/

	@Test
	void count() {
		RHyperLogLogReactive<Long> counter = this.client.getHyperLogLog("user:visits", LongCodec.INSTANCE);

		List<Long> longList = LongStream.rangeClosed(1, 25)
				.boxed()
				.collect(Collectors.toList());

		Mono<Boolean> addAllMono = counter.addAll(longList);

		//StepVerifier.create(addAllMono.then())
		StepVerifier.create(addAllMono)
				.expectNext(true)
				.verifyComplete();

		counter.count().doOnNext(System.out::println).subscribe();
	}
}
