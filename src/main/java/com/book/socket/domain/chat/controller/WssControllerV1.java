package com.book.socket.domain.chat.controller;

import com.book.socket.domain.chat.model.Message;
import com.book.socket.domain.chat.service.ChatServiceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class WssControllerV1 {

    private final ChatServiceV1 chatServiceV1;

    @MessageMapping("/chat/message/{from}")
    @SendTo("/sub/chat")
    public Message receivedMessage(
            @DestinationVariable String from,
            Message message
    ) {
        log.info("Message Received -> From: {}, to: {}, message: {}",
                from, message.getTo(), message.getFrom());
        chatServiceV1.saveChatMessage(message);
        return message;
    }


}
