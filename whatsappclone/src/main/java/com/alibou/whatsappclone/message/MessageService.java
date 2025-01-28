package com.alibou.whatsappclone.message;

import com.alibou.whatsappclone.chat.Chat;
import com.alibou.whatsappclone.chat.ChatRepository;
import com.alibou.whatsappclone.file.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
  private final MessageRepository messageRepository;
  private final ChatRepository chatRepository;
  private final MessageMapper messageMapper;
  private final FileService fileService;

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

  public void uploadMediaMessage(String chatId, MultipartFile file, Authentication authentication){
    Chat chat = chatRepository.findById(chatId)
            .orElseThrow(()-> new EntityNotFoundException("Chat not found"));
    final String senderId = getSenderId(chat,authentication);
    final String recipientId = getRecipientId(chat,authentication);
    final String filePath = fileService.saveFile(file,senderId);
    Message message=new Message();
    message.setChat(chat);
    message.setSenderId(senderId);
    message.setReceiverId(recipientId);
    message.setType(MessageType.IMAGE);
    message.setMediaFilePath(filePath);
    message.setState(MessageState.SENT);
    messageRepository.save(message);
  }

  private String getSenderId(Chat chat, Authentication authentication) {
    if(chat.getSender().getId().equals(authentication.getName())){
      return chat.getSender().getId();
    }
    return chat.getRecipient().getId();
  }

  private String getRecipientId(Chat chat, Authentication authentication) {
    if(chat.getSender().getId().equals(authentication.getName())){
      return chat.getRecipient().getId();
    }
    return chat.getSender().getId();
  }
}
