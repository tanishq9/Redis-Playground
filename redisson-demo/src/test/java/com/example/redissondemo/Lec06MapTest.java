package com.example.redissondemo;

import com.example.redissondemo.dto.Student;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapReactive;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec06MapTest extends BaseTest {

	@Test
	public void mapTest() {
		RMapReactive<String, String> map = this.client.getMap("user:1", StringCodec.INSTANCE);
		Mono<String> name = map.put("name", "sam");
		Mono<String> age = map.put("age", "20");
		Mono<String> city = map.put("city", "atlanta");

		StepVerifier.create(name.concatWith(age).concatWith(city).then())
				.verifyComplete();
	}

	@Test
	public void mapTest2() {
		// Map<Integer, Object>
		TypedJsonJacksonCodec typedJsonJacksonCodec = new TypedJsonJacksonCodec(Integer.class, Student.class);
		RMapReactive<Integer, Student> map = this.client.getMap("user:2", typedJsonJacksonCodec);

		Mono<Student> one = map.put(1, new Student("sam", 10, "atlanta", List.of(1, 2)));
		Mono<Student> two = map.put(2, new Student("jake", 30, "miami", List.of(10, 20)));

		StepVerifier.create(one.concatWith(two).then())
				.verifyComplete();
	}
}
