package com.book.socket.domain.repository;

import com.book.socket.domain.repository.entity.Chat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findTop10BySenderOrReceiverOrderByIdDesc(String sender, String receiver);

    // 동일한 row query
    // @Query("SELECT c FROM chat AS c WHERE c.sender = :sender OR c.receiver = :receiver ORDER BY c.t_id DESC LIMIT 10")
    // List<Chat> findTop10Chats(@Param("sender") String sender, @Param("receiver") String receiver);


}
