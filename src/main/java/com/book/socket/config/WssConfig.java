package com.book.socket.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//@ EnableWebSocket
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Configuration
public class WssConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry register) {
        register.addEndpoint("/ws-stomp")
                .setAllowedOrigins("*");
        // withSockJS(); 클라이언트가 웹소켓 사용할 수 없는 환경에서 대안의 방어 로직이다.
        // 프록시, 방화벽, 낮은 브라우저 버전 등의 웹 소켓을 지원하지 않으면 롱 콜링으로 구현한다.
        // 실무에서는 사용하는 것이 좋다고 한다. 공부하면 된다.
    }

    // row level socket config  (implements WebSocketConfigurer)
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(null, "/ws/v1/chat")
//                .setAllowedOrigins("*");
//    }
}