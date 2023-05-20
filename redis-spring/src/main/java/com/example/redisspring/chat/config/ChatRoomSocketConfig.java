package com.example.redisspring.chat.config;

import com.example.redisspring.chat.service.ChatRoomService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

@Configuration
public class ChatRoomSocketConfig {

	@Autowired
	private ChatRoomService chatRoomService;

	@Bean
	public HandlerMapping handlerMapping() {
		// Way to invoke web socket handler service
		Map<String, WebSocketHandler> stringChatRoomServiceMap = Map.of(
				"/chat", chatRoomService
		);
		return new SimpleUrlHandlerMapping(stringChatRoomServiceMap, -1);
	}
}
