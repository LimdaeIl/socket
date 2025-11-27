package com.book.socket.component;

import com.book.socket.domain.chat.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
@Component
public class WssHandlerV1 extends TextWebSocketHandler {

    // row level socket 사용 방법으로 대략적인 틀이므로, 실행 안됩니다.

    private final ObjectMapper objectMapper = new  ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        try {
            String payload = textMessage.getPayload();
            Message message = objectMapper.readValue(payload, Message.class);
            // 1. DB에 있는 데이터 인지 [from, to]
            // 2. 채팅 메시지 데이터 저장

            session.sendMessage(new TextMessage(payload));
        } catch (Exception e) {

        }
    }
}
