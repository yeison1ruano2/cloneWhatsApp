package com.alibou.whatsappclone.chat;

import com.alibou.whatsappclone.common.BaseAuditingEntity;
import com.alibou.whatsappclone.message.Message;
import com.alibou.whatsappclone.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static jakarta.persistence.GenerationType.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="chat")
public class Chat extends BaseAuditingEntity {

  @Id
  @GeneratedValue(strategy = UUID)
  private String id;
  @ManyToOne
  @JoinColumn(name="sender_id")
  private User sender;
  @ManyToOne
  @JoinColumn(name="recipient_id")
  private User recipient;
  @OneToMany(mappedBy = "chat", fetch=FetchType.EAGER)
  @OrderBy("createdDate DESC")
  private List<Message> messages;
}
