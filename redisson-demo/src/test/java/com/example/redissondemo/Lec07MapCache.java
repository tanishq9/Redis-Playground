package com.example.redissondemo;

import com.example.redissondemo.dto.Student;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapCacheReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec07MapCache extends BaseTest {

	// Using MapCache in Redisson we can set timeout for individual fields of a Map in Redis

	@Test
	void mapCacheTest() throws InterruptedException {
		TypedJsonJacksonCodec typedJsonJacksonCodec = new TypedJsonJacksonCodec(Integer.class, Student.class);
		RMapCacheReactive<Integer, Student> mapCache = this.client.getMapCache("users:cache", typedJsonJacksonCodec);

		Student student1 = new Student("sam", 10, "atlanta", List.of(1, 2));
		Student student2 = new Student("jake", 30, "miami", List.of(10, 20));

		Mono<Student> studentMono1 = mapCache.put(1, student1, 5, TimeUnit.SECONDS);
		Mono<Student> studentMono2 = mapCache.put(2, student2, 10, TimeUnit.SECONDS);

		StepVerifier.create(studentMono1.concatWith(studentMono2).then())
				.verifyComplete();

		mapCache.get(1).doOnNext(System.out::println).subscribe();
		mapCache.get(2).doOnNext(System.out::println).subscribe();

		Thread.sleep(6000);

		// Access students
		mapCache.get(1).doOnNext(System.out::println).subscribe();
		mapCache.get(2).doOnNext(System.out::println).subscribe();

		Thread.sleep(6000);
		mapCache.get(1).doOnNext(System.out::println).subscribe();
		mapCache.get(2).doOnNext(System.out::println).subscribe();
	}
}
