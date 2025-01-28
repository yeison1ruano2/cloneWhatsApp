package com.alibou.whatsappclone.message;

import com.alibou.whatsappclone.chat.Chat;
import com.alibou.whatsappclone.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="messages")
@NamedQuery(name=MessageConstants.FIND_MESSAGES_BY_CHAT_ID,
            query="SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.createdDate")
@NamedQuery(name=MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT,
            query= "UPDATE message SET state = :newState WHERE chat.id = :chatId")
public class Message extends BaseAuditingEntity {

  @Id
  @SequenceGenerator(name="msg_seq",sequenceName = "msg_seq",allocationSize = 1)
  @GeneratedValue(strategy =  SEQUENCE, generator = "msg_seq")
  private Long id;
  @Column(columnDefinition = "TEXT")
  private String content;
  @Enumerated(EnumType.STRING)
  private MessageState state;
  private MessageType type;
  @ManyToOne
  @JoinColumn(name="chat_id")
  private Chat chat;
  @Column(name="sender_id",nullable = false)
  private String senderId;
  @Column(name="receiver_id",nullable = false)
  private String receiverId;
  private String mediaFilePath;
}
