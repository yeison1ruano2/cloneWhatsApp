import { Component, OnInit } from '@angular/core';
import { ChatResponse } from '../../services/models';
import { ChatService } from '../../services/services';
import { ChatListComponent } from '../../components/chat-list/chat-list.component';

@Component({
  selector: 'app-main',
  imports: [ChatListComponent],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss',
})
export class MainComponent implements OnInit {
  chats: Array<ChatResponse> = [];

  constructor(private chatService: ChatService) {}

  ngOnInit(): void {
    this.getAllChats();
  }

  private getAllChats() {
    this.chatService.getChatsByReceiver().subscribe({
      next: (res) => {
        this.chats = res;
      },
    });
  }
}
