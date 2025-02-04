package com.alibou.whatsappclone.user;

import com.alibou.whatsappclone.chat.Chat;
import com.alibou.whatsappclone.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
@NamedQuery(name=UserConstants.FIND_USER_BY_EMAIL,
            query="SELECT u FROM User u WHERE u.email = :email")
@NamedQuery(name=UserConstants.FIND_ALL_USERS_EXCEPT_SELF,
            query="SELECT u FROM User u wHERE u.id != : publicId")
@NamedQuery(name=UserConstants.FIND_USER_BY_PUBLIC_ID,
            query="SELECT u FROM User u wHERE u.id = : publicId")
public class User extends BaseAuditingEntity {

  private static final int LAST_ACTIVE_INTERVAL = 5;

  @Id
  private String id;
  private String firstname;
  private String lastname;
  private String email;
  private LocalDateTime lastSeen;

  @OneToMany(mappedBy = "sender")
  private List<Chat> chatsAsSender;
  @OneToMany(mappedBy = "recipient")
  private List<Chat> chatsAsRecipient;

  @Transient
  public boolean isUserOnLine(){
    return lastSeen != null && lastSeen.isBefore(LocalDateTime.now().minusMinutes(LAST_ACTIVE_INTERVAL));
  }
}
