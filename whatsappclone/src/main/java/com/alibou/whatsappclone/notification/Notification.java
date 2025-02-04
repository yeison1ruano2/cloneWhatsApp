package com.alibou.whatsappclone.notification;

import com.alibou.whatsappclone.message.MessageType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

  private String chatId;
  private String content;
  private String senderId;
  private String receiverId;
  private String chatName;
  private MessageType messageType;
  private NotificationType type;
  private byte[] media;
}
