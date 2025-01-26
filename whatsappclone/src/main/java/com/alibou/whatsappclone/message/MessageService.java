package com.alibou.whatsappclone.message;

import com.alibou.whatsappclone.chat.Chat;
import com.alibou.whatsappclone.chat.ChatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
  private final MessageRepository messageRepository;
  private final ChatRepository chatRepository;
  private final MessageMapper messageMapper;

  public void saveMessage(MessageRequest messageRequest){
    Chat chat = chatRepository.findById(messageRequest.getChatId())
            .orElseThrow(()-> new EntityNotFoundException("Chat no found"));
    Message message=new Message();
    message.setContent(messageRequest.getContent());
    message.setChat(chat);
    message.setSenderId(messageRequest.getSenderId());
    message.setReceiverId(messageRequest.getReceiverId());
    message.setType(messageRequest.getType());
    message.setState(MessageState.SENT);
    messageRepository.save(message);
  }

  public List<MessageResponse> findChatMessages(String chatId){
    return messageRepository.findMessagesByChatId(chatId)
            .stream()
            .map(messageMapper::toMessageResponse)
            .toList();
  }
  @Transactional
  public void setMessageToSeen(String chatId, Authentication authentication){
    Chat chat = chatRepository.findById(chatId)
            .orElseThrow(()-> new EntityNotFoundException("Chat not found"));
    //final String recipientId = getRecipientId(chat,authentication);
    messageRepository.setMessageToSeenByChatId(chatId,MessageState.SEEN);

  }

  private String getRecipientId(Chat chat, Authentication authentication) {
    if(chat.getSender().getId().equals(authentication.getName())){
      return chat.getRecipient().getId();
    }
    return chat.getSender().getId();
  }
}
