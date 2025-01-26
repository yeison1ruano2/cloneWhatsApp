package com.alibou.whatsappclone.message;

import com.alibou.whatsappclone.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message,Long> {
  @Query(name=MessageConstants.FIND_MESSAGES_BY_CHAT_ID)
  List<Message> findMessagesByChatId(String chatId);

  @Query(name=MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT)
  @Modifying
  void setMessageToSeenByChatId(@Param("chatId") String chatId, @Param("newState") MessageState state);
}
