package com.book.socket.domain.chat.service;

import com.book.socket.domain.chat.model.Message;
import com.book.socket.domain.chat.model.response.ChatListResponse;
import com.book.socket.domain.repository.ChatRepository;
import com.book.socket.domain.repository.entity.Chat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatServiceV1 {

    private final ChatRepository chatRepository;

    public ChatListResponse chatList(String from, String to) {
        List<Chat> chats = chatRepository.findTop10BySenderOrReceiverOrderByIdDesc(from, to);
        List<Message> res = chats.stream()
                .map(chat -> new Message(
                        chat.getReceiver(),
                        chat.getSender(),
                        chat.getMessage()
                )).toList();
        return new ChatListResponse(res);
    }

    // 하나의 통신으로만 하는 소켓은 트랜잭션의 경계가 명확하지 않아서 잘 정해야한다.
    // save(), flush() 직접 호출로도 여전히 문제가 존재한다.
    // 트랜잭션의 경계를 명확하게 하기 위해 설정한 것이다.
    // JPA를 자주 사용하지만, JDBC 를 사용하는 경우도 있다.
    // row level에서 다루기 위해 JDBC를 사용하고, 직접 쿼리를 작성해야 한다는 단점이 있다.
    // row level의 단점은 많은 리소스를 들어간다는 단점이 명확하다. 최적화된 sql 문을 제어함으로써
    // 서비스를 튜닝하는 이점이 있다. 그러나 유지보수하는 입장에서는 매우 어려워진다.
    @Transactional(transactionManager = "createChatTransactionManager")
    public void saveChatMessage(Message message) {
        Chat chat = Chat.builder()
                .sender(message.getFrom())
                .receiver(message.getTo())
                .message(message.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        chatRepository.save(chat);
    }
}
