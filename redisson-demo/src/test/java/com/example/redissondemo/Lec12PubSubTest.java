package com.example.redissondemo;

import org.junit.jupiter.api.Test;
import org.redisson.api.RPatternTopicReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.listener.PatternMessageListener;
import org.redisson.client.codec.StringCodec;

public class Lec12PubSubTest extends BaseTest {

	@Test
	void subscriber1() throws InterruptedException {
		// Topic is like a slack-channel, where everyone can publish and listen to updates
		RTopicReactive topic = this.client.getTopic("slack-channel", StringCodec.INSTANCE);

		topic.getMessages(String.class)
				.doOnError(System.out::println)
				.doOnNext(System.out::println)
				.subscribe();

		Thread.sleep(60000);
	}

	@Test
	void subscriber2() throws InterruptedException {
		// Topic is like a slack-channel, where everyone can publish and listen to updates
		RTopicReactive topic = this.client.getTopic("slack-channel", StringCodec.INSTANCE);

		topic.getMessages(String.class)
				.doOnError(System.out::println)
				.doOnNext(System.out::println)
				.subscribe();

		Thread.sleep(60000);
	}

	@Test
	void subscriberPatternTopic() throws InterruptedException {
		RPatternTopicReactive patternTopic = this.client.getPatternTopic("slack-channel*", StringCodec.INSTANCE);

		patternTopic.addListener(String.class, new PatternMessageListener<String>() {
			@Override
			public void onMessage(CharSequence pattern, CharSequence topic, String msg) {
				System.out.println("pattern: " + pattern + ", topic: " + topic + ", message: " + msg);
			}
		}).subscribe();

		Thread.sleep(60000);
	}
}
