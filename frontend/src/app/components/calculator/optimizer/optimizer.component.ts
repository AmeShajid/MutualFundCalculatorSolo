import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { PortfolioService } from '../../../services/portfolio.service';
import { PortfolioRecommendation } from '../../../models/portfolio-recommendation.model';

@Component({
  selector: 'app-optimizer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './optimizer.component.html',
  styleUrls: ['./optimizer.component.css']
})
export class OptimizerComponent {
  private destroy$ = new Subject<void>();

  @Input() selectedTickers: string[] = [];
  @Input() principal: number = 0;
  @Input() years: number = 0;

  riskTolerance: string = 'moderate';
  portfolioResult: PortfolioRecommendation | null = null;
  isPortfolioLoading: boolean = false;
  portfolioError: string = '';

  constructor(private portfolioService: PortfolioService) {}

  onOptimize(): void {
    this.portfolioError = '';
    this.portfolioResult = null;

    if (this.selectedTickers.length === 0) {
      this.portfolioError = 'Please select at least one mutual fund.';
      return;
    }
    if (this.principal <= 0) {
      this.portfolioError = 'Please enter an initial investment amount greater than 0.';
      return;
    }
    if (this.years <= 0) {
      this.portfolioError = 'Please enter a time horizon greater than 0 years.';
      return;
    }

    this.isPortfolioLoading = true;

    this.portfolioService.optimize({
      tickers: this.selectedTickers,
      riskTolerance: this.riskTolerance,
      principal: this.principal,
      years: this.years
    }).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: PortfolioRecommendation) => {
          this.portfolioResult = data;
          this.isPortfolioLoading = false;
        },
        error: (err) => {
          this.portfolioError = err.error?.message || 'Portfolio optimization failed. Please try again.';
          this.isPortfolioLoading = false;
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
