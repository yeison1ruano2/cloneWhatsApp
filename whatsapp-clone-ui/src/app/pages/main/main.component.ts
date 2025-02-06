import { Component, OnInit } from '@angular/core';
import { ChatResponse, MessageResponse } from '../../services/models';
import { ChatService, MessageService } from '../../services/services';
import { ChatListComponent } from '../../components/chat-list/chat-list.component';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';

@Component({
  selector: 'app-main',
  imports: [ChatListComponent],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss',
})
export class MainComponent implements OnInit {
  chats: Array<ChatResponse> = [];
  selectedChat: ChatResponse = {};
  chatMessages: Array<MessageResponse> = [];

  constructor(
    private readonly chatService: ChatService,
    private readonly keycloakService: KeycloakService,
    private readonly messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.getAllChats();
  }

  private getAllChatMessages(chatId: string) {
    this.messageService
      .getMessages({
        'chat-id': chatId,
      })
      .subscribe({
        next: (messages) => {
          this.chatMessages = messages;
        },
      });
  }

  private setMessagesToSeen() {
    this.messageService
      .setMessageToSeen({
        'chat-id': this.selectedChat.id as string,
      })
      .subscribe({
        next: () => {},
      });
  }

  private getAllChats() {
    this.chatService.getChatsByReceiver().subscribe({
      next: (res) => {
        this.chats = res;
      },
    });
  }

  userProfile() {
    this.keycloakService.accountManagement();
  }
  logout() {
    this.keycloakService.logout();
  }

  chatSelected(chatResponse: ChatResponse) {
    this.selectedChat = chatResponse;
    this.getAllChatMessages(chatResponse.id as string);
    this.setMessagesToSeen();
    //this.selectedChat.unreadCount = 0;
  }

  isSelfMessage(message: MessageResponse) {
    return message.senderId === this.keycloakService.UserId;
  }
}
