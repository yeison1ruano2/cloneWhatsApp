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
}
