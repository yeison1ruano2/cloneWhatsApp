package com.alibou.whatsappclone.message;

import com.alibou.whatsappclone.chat.Chat;
import com.alibou.whatsappclone.chat.ChatRepository;
import com.alibou.whatsappclone.file.FileService;
import com.alibou.whatsappclone.file.FileUtils;
import com.alibou.whatsappclone.notification.Notification;
import com.alibou.whatsappclone.notification.NotificationService;
import com.alibou.whatsappclone.notification.NotificationType;
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
  private final NotificationService notificationService;

  public void saveMessage(MessageRequest messageRequest){
    Chat chat = chatRepository.findById(messageRequest.getChatId())
            .orElseThrow(()-> new EntityNotFoundException("Chat was not found"));
    Message message=new Message();
    message.setContent(messageRequest.getContent());
    message.setChat(chat);
    message.setSenderId(messageRequest.getSenderId());
    message.setReceiverId(messageRequest.getReceiverId());
    message.setType(messageRequest.getType());
    message.setState(MessageState.SENT);
    messageRepository.save(message);
    Notification notification = Notification
            .builder()
            .chatId(chat.getId())
            .messageType(messageRequest.getType())
            .content(messageRequest.getContent())
            .senderId(messageRequest.getSenderId())
            .receiverId(messageRequest.getReceiverId())
            .type(NotificationType.MESSAGE)
            .chatName(chat.getChatName(message.getSenderId()))
            .build();
    notificationService.sendNotification(message.getReceiverId(),notification);
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
    final String recipientId = getRecipientId(chat,authentication);
    messageRepository.setMessageToSeenByChatId(chatId,MessageState.SEEN);
    Notification notification = Notification
            .builder()
            .type(NotificationType.SEEN)
            .receiverId(recipientId)
            .chatId(chat.getId())
            .senderId(getSenderId(chat,authentication))
            .build();
    notificationService.sendNotification(recipientId,notification);

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

    Notification notification = Notification
            .builder()
            .chatId(chat.getId())
            .type(NotificationType.IMAGE)
            .messageType(MessageType.IMAGE)
            .senderId(senderId)
            .receiverId(recipientId)
            .media(FileUtils.readFileFromLocation(filePath))
            .build();
    notificationService.sendNotification(recipientId,notification);
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
