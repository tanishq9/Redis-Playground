package com.example.redissondemo.config;

import java.util.Objects;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;

public class RedissonConfig {

	private RedissonClient redissonClient;

	private RedissonClient getClient() {
		if (Objects.isNull(this.redissonClient)) {
			Config config = new Config();
			config.useSingleServer()
					.setAddress("redis://127.0.0.1:6379");
			redissonClient = Redisson.create(config);
		}
		return redissonClient;
	}

	public RedissonReactiveClient getReactiveClient() {
		return getClient().reactive();
	}
}
