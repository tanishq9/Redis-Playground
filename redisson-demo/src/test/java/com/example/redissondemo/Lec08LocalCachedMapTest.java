package com.example.redissondemo;

import com.example.redissondemo.dto.Student;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.config.Config;
import reactor.core.publisher.Flux;

public class Lec08LocalCachedMapTest extends BaseTest {

/*
There are 3 different types of Sync strategy, before that, we know - When someone updates the Redis Map, the update will be sent to Redis.
- None: The other app server would not be informed by Redis i.e. do not sync.
- Invalidate: The other app server would have this key removed, only when that invalidated key/field is requested then only it would be fetched from Redis.
- Update: The other app server would have the updated key/field i.e. updates by someone are reflected immediately incase the sync strategy is Update for the LocalCachedMap.

ReconnectStrategy
- Clear: When the connection is back up, now LocalCachedMap will be clearing all local cache and get from Redis.
- None: Keep serving old data, if there are any future updates then LocalCachedMap will get those.
*/

	RLocalCachedMap<Integer, Student> studentsMap;

	@BeforeAll
	void setupClient() {
		Config config = new Config();
		config.useSingleServer().setAddress("redis://127.0.0.1:6379");
		RedissonClient redissonClient = Redisson.create(config);

		LocalCachedMapOptions<Integer, Student> localCachedMapOptions = LocalCachedMapOptions.<Integer, Student>defaults()
				//.timeToLive(60, TimeUnit.SECONDS) // time to keep local cache then it will invalidate and refresh from Redis
				.syncStrategy(LocalCachedMapOptions.SyncStrategy.NONE)
				.reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);

		studentsMap = redissonClient.getLocalCachedMap(
				"students",
				new TypedJsonJacksonCodec(Integer.class, Student.class),
				localCachedMapOptions
		);
	}

	@Test
	public void appServer1() throws InterruptedException {
		Student student1 = new Student("sam", 10, "atlanta", List.of(1, 2));
		Student student2 = new Student("jake", 30, "miami", List.of(10, 20));
		this.studentsMap.put(1, student1);
		this.studentsMap.put(2, student2);

		Flux.interval(Duration.ofSeconds(1))
				.doOnNext(i -> System.out.println(i + " ==> " + studentsMap.get(1)))
				.subscribe();

		Thread.sleep(600000);
	}

	@Test
	public void appServer2() {
		Student student1 = new Student("sam-updated", 10, "atlanta", List.of(1, 2));
		this.studentsMap.put(1, student1);
	}
}
