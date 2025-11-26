package com.book.socket.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@EnableWebSocket
@RequiredArgsConstructor
@Configuration
public class WssConfig implements WebSocketConfigurer {

//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/sub");
//        registry.setApplicationDestinationPrefixes("/pub");
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry register) {
//        register.addEndpoint("/ws-stomp")
//                .setAllowedOrigins("*");
//    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(null, "/ws/v1/chat")
                .setAllowedOrigins("*");

    }
}