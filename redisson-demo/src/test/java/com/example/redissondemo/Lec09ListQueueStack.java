package com.example.redissondemo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.redisson.api.RDequeReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RQueueReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec09ListQueueStack extends BaseTest {

	@Test
	void listTest() {
		RListReactive<Long> list = this.client.getList("number-input", LongCodec.INSTANCE);

		List<Long> longList = LongStream.rangeClosed(1, 10)
				.boxed()
				.collect(Collectors.toList());

		Mono<Void> listAdd = Flux.range(1, 10)
				.map(Integer::longValue)
				.flatMap(list::add)
				.then();

		/*StepVerifier.create(listAdd)
				.verifyComplete();*/

		StepVerifier.create(list.addAll(longList).then())
				.verifyComplete();

		StepVerifier.create(list.size())
				.expectNext(10)
				.verifyComplete();
	}

	@Test
	void queueTest() {
		RQueueReactive<Long> queue = this.client.getQueue("number-input", LongCodec.INSTANCE);

		queue.poll().subscribe(); // 1
		queue.poll().subscribe(); // 2, Queue is FIFO

		queue.size().subscribe(val -> System.out.println("Size is: " + val));
	}

	@Test
	void stackTest() {
		// Deque can be used both as a stack or queue
		RDequeReactive<Object> deque = this.client.getDeque("number-input", LongCodec.INSTANCE);

		deque.pollLast().subscribe(); // 1
		deque.pollLast().subscribe(); // 2, Stack is LIFO

		deque.size().subscribe(val -> System.out.println("Size is: " + val));
	}
}
