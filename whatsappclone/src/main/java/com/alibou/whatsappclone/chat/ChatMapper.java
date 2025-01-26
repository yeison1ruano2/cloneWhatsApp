package com.alibou.whatsappclone.chat;

import org.springframework.stereotype.Service;

@Service
public class ChatMapper {
  public ChatResponse tochatResponse(Chat chat, String senderId) {
    return ChatResponse.builder()
            .id(chat.getId())
            .name(chat.getChatName(senderId))
            .unreadCount(chat.getUnreadMessages(senderId))
            .lastMessage(chat.getLastMessage())
            .isRecipientOnline(chat.getRecipient().isUserOnLine())
            .senderId(chat.getSender().getId())
            .receiverId(chat.getRecipient().getId())
            .build();
  }
}
