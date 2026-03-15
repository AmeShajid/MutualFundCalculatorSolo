import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { PortfolioSidebarComponent, SelectedFund } from './portfolio-sidebar/portfolio-sidebar.component';
import { PortfolioSummaryComponent } from './portfolio-summary/portfolio-summary.component';
import { PortfolioChartComponent } from './portfolio-chart/portfolio-chart.component';
import { PortfolioHoldingsComponent } from './portfolio-holdings/portfolio-holdings.component';
import { PortfolioReasoningComponent } from './portfolio-reasoning/portfolio-reasoning.component';

import { FundService } from '../../services/fund.service';
import { PortfolioService } from '../../services/portfolio.service';
import { Fund } from '../../models/fund.model';
import { ChatMessage } from '../../models/chat-message.model';
import { PortfolioRecommendation } from '../../models/portfolio-recommendation.model';

const FUND_COLORS = ['#1a6fba', '#e85d3a', '#2ecc71', '#9b59b6', '#f39c12'];

@Component({
  selector: 'app-portfolio',
  standalone: true,
  imports: [
    CommonModule,
    PortfolioSidebarComponent,
    PortfolioSummaryComponent,
    PortfolioChartComponent,
    PortfolioHoldingsComponent,
    PortfolioReasoningComponent
  ],
  templateUrl: './portfolio.component.html',
  styleUrls: ['./portfolio.component.css']
})
export class PortfolioComponent implements OnInit, OnDestroy {

  private destroy$ = new Subject<void>();
  private colorIndex = 0;

  funds: Fund[] = [];
  selectedFunds: SelectedFund[] = [];
  fundColors: Map<string, string> = new Map();

  riskTolerance: string = 'moderate';
  principal: number = 10000;
  years: number = 10;

  recommendation: PortfolioRecommendation | null = null;

  messages: ChatMessage[] = [];
  isGenerating: boolean = false;
  isSending: boolean = false;

  constructor(
    private fundService: FundService,
    private portfolioService: PortfolioService
  ) {}

  ngOnInit(): void {
    this.fundService.getFunds().pipe(takeUntil(this.destroy$)).subscribe({
      next: (data: Fund[]) => {
        this.funds = data;
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onFundAdded(fund: Fund): void {
    if (this.selectedFunds.length >= 5) return;
    if (this.selectedFunds.some(f => f.symbol === fund.symbol)) return;

    const color = FUND_COLORS[this.colorIndex % FUND_COLORS.length];
    this.colorIndex++;

    this.selectedFunds = [...this.selectedFunds, {
      symbol: fund.symbol,
      name: fund.name,
      color
    }];
    this.rebuildColorMap();
  }

  onFundRemoved(symbol: string): void {
    this.selectedFunds = this.selectedFunds.filter(f => f.symbol !== symbol);
    this.rebuildColorMap();

    // Reset results if a fund is removed after generating
    if (this.recommendation) {
      this.recommendation = null;
      this.messages = [];
    }
  }

  onRiskToleranceChange(risk: string): void {
    this.riskTolerance = risk;
  }

  onPrincipalChange(value: number): void {
    this.principal = value;
  }

  onYearsChange(value: number): void {
    this.years = value;
  }

  onGenerate(): void {
    if (this.selectedFunds.length < 2 || this.isGenerating) return;
    if (this.principal <= 0 || this.years <= 0) return;

    this.isGenerating = true;
    this.messages = [];

    const tickers = this.selectedFunds.map(f => f.symbol);

    this.portfolioService.optimize({
      tickers,
      riskTolerance: this.riskTolerance,
      principal: this.principal,
      years: this.years
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: (data: PortfolioRecommendation) => {
        this.recommendation = data;
        this.isGenerating = false;
      },
      error: () => {
        this.isGenerating = false;
      }
    });
  }

  onSendFollowUp(text: string): void {
    if (!text.trim() || this.isSending) return;

    const userMsg: ChatMessage = {
      role: 'user',
      content: text,
      timestamp: new Date()
    };
    this.messages = [...this.messages, userMsg];

    const conversationHistory = this.messages.map(m => ({
      role: m.role === 'assistant' ? 'model' : 'user',
      content: m.content
    }));

    // Prepend reasoning as context
    if (this.recommendation) {
      conversationHistory.unshift({
        role: 'model',
        content: this.recommendation.reasoning
      });
    }

    this.isSending = true;
    const tickers = this.selectedFunds.map(f => f.symbol);

    this.portfolioService.chat({
      conversationHistory,
      tickers,
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
        this.messages = [...this.messages, assistantMsg];
        this.isSending = false;
      },
      error: () => {
        const errorMsg: ChatMessage = {
          role: 'assistant',
          content: 'Failed to get a response. Please try again.',
          timestamp: new Date()
        };
        this.messages = [...this.messages, errorMsg];
        this.isSending = false;
      }
    });
  }

  private rebuildColorMap(): void {
    this.fundColors = new Map();
    for (const sf of this.selectedFunds) {
      this.fundColors.set(sf.symbol, sf.color);
    }
  }
}
