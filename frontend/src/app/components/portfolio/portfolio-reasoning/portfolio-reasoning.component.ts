import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PortfolioRecommendation } from '../../../models/portfolio-recommendation.model';
import { ChatMessage } from '../../../models/chat-message.model';

@Component({
  selector: 'app-portfolio-reasoning',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './portfolio-reasoning.component.html',
  styleUrls: ['./portfolio-reasoning.component.css']
})
export class PortfolioReasoningComponent {

  @Input() recommendation: PortfolioRecommendation | null = null;
  @Input() messages: ChatMessage[] = [];
  @Input() isSending: boolean = false;

  @Output() sendFollowUp = new EventEmitter<string>();

  followUpText: string = '';

  onSend(): void {
    if (!this.followUpText.trim() || this.isSending) return;
    this.sendFollowUp.emit(this.followUpText.trim());
    this.followUpText = '';
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.onSend();
    }
  }
}
