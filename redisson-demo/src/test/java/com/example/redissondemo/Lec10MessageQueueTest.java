package com.example.redissondemo;

import java.time.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBlockingDequeReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec10MessageQueueTest extends BaseTest {

	private RBlockingDequeReactive<Long> msgQueue;

	@BeforeAll
	public void setupQueue() {
		// BlockingDeque is used for Message Queue
		msgQueue = this.client.getBlockingDeque("message-queue", LongCodec.INSTANCE);
	}

	@Test
	public void consumer1() throws InterruptedException {
		this.msgQueue.takeElements()
				.doOnNext(item -> System.out.println("Consumer 1: " + item))
				.doOnError(System.out::println)
				.subscribe();

		Thread.sleep(60000);
	}

	@Test
	public void consumer2() throws InterruptedException {
		this.msgQueue.takeElements()
				.doOnNext(item -> System.out.println("Consumer 2: " + item))
				.doOnError(System.out::println)
				.subscribe();

		Thread.sleep(60000);
	}

	@Test
	public void producer1() {
		Mono<Void> mono = Flux.range(1, 100)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(item -> System.out.println("Going to add: " + item))
				.flatMap(item -> this.msgQueue.add(item.longValue()))
				.then();

		StepVerifier.create(mono)
				.verifyComplete();
	}
}
