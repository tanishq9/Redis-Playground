package com.example.redisspring.chat.service;

import java.net.URI;
import org.redisson.api.RListReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChatRoomService implements WebSocketHandler {

	@Autowired
	private RedissonReactiveClient redissonReactiveClient;

	// webSocketSession is a persistent connection
	// ws://localhost:8080/chat
	@Override
	public Mono<Void> handle(WebSocketSession webSocketSession) {
		String chatRoom = "slack-room-" + getChatRoomName(webSocketSession);
		RTopicReactive topic = this.redissonReactiveClient.getTopic(chatRoom, StringCodec.INSTANCE);
		RListReactive<String> list = this.redissonReactiveClient.getList("history-" + chatRoom, StringCodec.INSTANCE);

		// webSocketSession is the bi-directional connection b/w backend and frontend

		webSocketSession.receive() // message received by server from consumer
				.map(WebSocketMessage::getPayloadAsText)
				.flatMap(msg -> list.add(msg).then(topic.publish(msg))) // publish message to Redis (maintain state) from server and store history
				.doOnError(System.out::println)
				.doFinally(System.out::println)
				.subscribe();

		Flux<WebSocketMessage> messages = topic
				.getMessages(String.class)
				.startWith(list.iterator())
				//.startWith(topic.getMessages(String.class))
				.map(message -> webSocketSession.textMessage(message))
				.doOnError(System.out::println)
				.doFinally(System.out::println);

		return webSocketSession.send(messages); // message published by server to consumer, will be subscribed by the frontend/browser
	}

	// ws://localhost:8080/chat?room=demo
	private String getChatRoomName(WebSocketSession webSocketSession) {
		URI uri = webSocketSession.getHandshakeInfo().getUri();
		return UriComponentsBuilder.fromUri(uri)
				.build()
				.getQueryParams()
				.toSingleValueMap()
				.getOrDefault("room", "default");
	}
}
