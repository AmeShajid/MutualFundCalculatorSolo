import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { FundSelectComponent } from '../shared/fund-select/fund-select.component';
import { FundService } from '../../services/fund.service';
import { PortfolioService } from '../../services/portfolio.service';
import { Fund } from '../../models/fund.model';
import { ChatMessage } from '../../models/chat-message.model';
import { PortfolioRecommendation } from '../../models/portfolio-recommendation.model';

@Component({
  selector: 'app-portfolio',
  standalone: true,
  imports: [CommonModule, FormsModule, FundSelectComponent],
  templateUrl: './portfolio.component.html',
  styleUrls: ['./portfolio.component.css']
})
export class PortfolioComponent implements OnInit, OnDestroy, AfterViewChecked {

  private destroy$ = new Subject<void>();
  private shouldScroll = false;

  @ViewChild('chatMessages') chatMessagesEl!: ElementRef;

  funds: Fund[] = [];
  selectedTickers: string[] = [];
  riskTolerance: string = 'moderate';
  years: number = 0;
  principal: number = 0;
  maxSelections: number = 5;

  messages: ChatMessage[] = [];
  followUpText: string = '';
  isGenerating: boolean = false;
  isSending: boolean = false;
  errorMessage: string = '';

  constructor(
    private fundService: FundService,
    private portfolioService: PortfolioService
  ) {}

  ngOnInit(): void {
    this.fundService.getFunds().pipe(takeUntil(this.destroy$)).subscribe({
      next: (data: Fund[]) => {
        this.funds = data;
      },
      error: () => {
        this.errorMessage = 'Could not load funds. Make sure the backend is running.';
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngAfterViewChecked(): void {
    if (this.shouldScroll) {
      this.scrollToBottom();
      this.shouldScroll = false;
    }
  }

  private scrollToBottom(): void {
    if (this.chatMessagesEl) {
      const el = this.chatMessagesEl.nativeElement;
      el.scrollTop = el.scrollHeight;
    }
  }

  onGenerate(): void {
    this.errorMessage = '';

    if (this.selectedTickers.length === 0) {
      this.errorMessage = 'Please select at least one fund.';
      return;
    }
    if (this.principal <= 0) {
      this.errorMessage = 'Please enter an investment amount greater than 0.';
      return;
    }
    if (this.years <= 0) {
      this.errorMessage = 'Please enter a time horizon greater than 0 years.';
      return;
    }

    this.isGenerating = true;

    // Add synthetic user message
    const userMsg: ChatMessage = {
      role: 'user',
      content: `Generate portfolio for ${this.selectedTickers.join(', ')} with $${this.principal.toLocaleString()} over ${this.years} years, ${this.riskTolerance} risk.`,
      timestamp: new Date()
    };
    this.messages.push(userMsg);
    this.shouldScroll = true;

    this.portfolioService.optimize({
      tickers: this.selectedTickers,
      riskTolerance: this.riskTolerance,
      principal: this.principal,
      years: this.years
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: (data: PortfolioRecommendation) => {
        const assistantMsg: ChatMessage = {
          role: 'assistant',
          content: data.reasoning,
          timestamp: new Date(),
          portfolioData: data
        };
        this.messages.push(assistantMsg);
        this.isGenerating = false;
        this.shouldScroll = true;
      },
      error: (err) => {
        const errorMsg: ChatMessage = {
          role: 'assistant',
          content: err.error?.message || 'Portfolio optimization failed. Please try again.',
          timestamp: new Date()
        };
        this.messages.push(errorMsg);
        this.isGenerating = false;
        this.shouldScroll = true;
      }
    });
  }

  onSendFollowUp(): void {
    if (!this.followUpText.trim() || this.isSending) return;

    const userMsg: ChatMessage = {
      role: 'user',
      content: this.followUpText.trim(),
      timestamp: new Date()
    };
    this.messages.push(userMsg);
    this.shouldScroll = true;

    const conversationHistory = this.messages.map(m => ({
      role: m.role === 'assistant' ? 'model' : 'user',
      content: m.portfolioData
        ? m.content + '\n\n[Portfolio data was shown with allocations]'
        : m.content
    }));

    this.followUpText = '';
    this.isSending = true;

    this.portfolioService.chat({
      conversationHistory,
      tickers: this.selectedTickers,
      riskTolerance: this.riskTolerance,
      principal: this.principal,
      years: this.years
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: (data) => {
        const assistantMsg: ChatMessage = {
          role: 'assistant',
          content: data.reply,
          timestamp: new Date()
        };
        this.messages.push(assistantMsg);
        this.isSending = false;
        this.shouldScroll = true;
      },
      error: () => {
        const errorMsg: ChatMessage = {
          role: 'assistant',
          content: 'Failed to get a response. Please try again.',
          timestamp: new Date()
        };
        this.messages.push(errorMsg);
        this.isSending = false;
        this.shouldScroll = true;
      }
    });
  }

  onFollowUpKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.onSendFollowUp();
    }
  }
}
