package com.example.redissondemo;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RSetReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec13BatchTest extends BaseTest {

	@Test
	void batchTest() {
		RBatchReactive batch = this.client.createBatch(BatchOptions.defaults());
		RListReactive<Long> list = batch.getList("numbers-list", LongCodec.INSTANCE);
		RSetReactive<Long> set = batch.getSet("numbers-set", LongCodec.INSTANCE);

		for (long i = 0; i < 500_000; i++) {
			list.add(i);
			set.add(i);
		}

		// batch.execute();

		StepVerifier.create(batch.execute().then())
				.verifyComplete();
	}

	@Test
	void regularTest() {
		RListReactive<Long> list = client.getList("numbers-list-r", LongCodec.INSTANCE);
		RSetReactive<Long> set = client.getSet("numbers-set-r", LongCodec.INSTANCE);

		Mono<Void> mono1 = Flux.range(1, 500000)
				.map(Integer::longValue)
				.flatMap(list::add)
				.then();

		Mono<Void> mono2 = Flux.range(1, 500000)
				.map(Integer::longValue)
				.flatMap(set::add)
				.then();

		// batch.execute();

		StepVerifier.create(mono1.concatWith(mono2))
				.verifyComplete();
	}
}
