import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PortfolioRecommendation } from '../../../models/portfolio-recommendation.model';
import { ChatMessage } from '../../../models/chat-message.model';

@Component({
  selector: 'app-portfolio-reasoning',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './portfolio-reasoning.component.html',
  styleUrls: ['./portfolio-reasoning.component.css']
})
export class PortfolioReasoningComponent {

  @Input() recommendation: PortfolioRecommendation | null = null;
  @Input() messages: ChatMessage[] = [];
  @Input() isSending: boolean = false;
}
