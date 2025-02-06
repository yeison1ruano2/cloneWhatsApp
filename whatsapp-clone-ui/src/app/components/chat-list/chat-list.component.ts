import { Component, input, InputSignal, output } from '@angular/core';
import { ChatResponse } from '../../services/models/chat-response';
import { DatePipe } from '@angular/common';
import { UserResponse } from '../../services/models';
import { ChatService, UserService } from '../../services/services';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';

@Component({
  selector: 'app-chat-list',
  imports: [DatePipe],
  templateUrl: './chat-list.component.html',
  styleUrl: './chat-list.component.scss',
})
export class ChatListComponent {
  chats: InputSignal<ChatResponse[]> = input<ChatResponse[]>([]);
  searchNewContact: boolean = false;
  contacts: Array<UserResponse> = [];
  chatSelected = output<ChatResponse>();

  constructor(
    private readonly userService: UserService,
    private readonly chatService: ChatService,
    private readonly keycloakService: KeycloakService
  ) {}

  wrapMessage(lastMessage: string | undefined): string {
    if (lastMessage && lastMessage.length <= 20) {
      return lastMessage;
    }
    return lastMessage?.substring(0, 17) + '...';
  }
  chatClicked(chat: ChatResponse) {
    this.chatSelected.emit(chat);
  }
  searchContact() {
    this.userService.getUsers().subscribe({
      next: (users) => {
        this.contacts = users;
        this.searchNewContact = true;
      },
    });
  }

  selectContact(contact: UserResponse) {
    this.chatService
      .createChat({
        'sender-id': this.keycloakService.UserId,
        'receiver-id': contact.id as string,
      })
      .subscribe({
        next: (res) => {
          const chat: ChatResponse = {
            id: res.response,
            name: contact.firstName + ' ' + contact.lastName,
            recipientOnline: contact.online,
            lastMessageTime: contact.lastSeen,
            senderId: this.keycloakService.UserId,
            receiverId: contact.id,
          };
          this.chats().unshift(chat);
          this.searchNewContact = false;
          this.chatSelected.emit(chat);
        },
      });
  }
}
