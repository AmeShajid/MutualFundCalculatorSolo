import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { FundService } from '../../services/fund.service';
import { PredictionService } from '../../services/prediction.service';
import { PortfolioService } from '../../services/portfolio.service';
import { Fund } from '../../models/fund.model';
import { ComparisonResponse } from '../../models/comparison-response.model';
import { PortfolioRecommendation } from '../../models/portfolio-recommendation.model';
import { FundSelectComponent } from '../shared/fund-select/fund-select.component';

@Component({
  selector: 'app-calculator',
  standalone: true,
  templateUrl: './calculator.component.html',
  styleUrls: ['./calculator.component.css'],
  imports: [CommonModule, FormsModule, FundSelectComponent]
})
export class CalculatorComponent implements OnInit, OnDestroy {

  private destroy$ = new Subject<void>();

  funds: Fund[] = [];
  selectedTickers: string[] = [];
  principal: number = 0;
  years: number = 0;
  comparisonResult: ComparisonResponse | null = null;
  isLoading: boolean = false;
  errorMessage: string = '';
  maxSelections: number = 5;

  riskTolerance: string = 'moderate';
  portfolioResult: PortfolioRecommendation | null = null;
  isPortfolioLoading: boolean = false;
  portfolioError: string = '';

  constructor(
    private fundService: FundService,
    private predictionService: PredictionService,
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

  private validateInputs(errorTarget: 'calculate' | 'portfolio'): boolean {
    const setError = (msg: string) => {
      if (errorTarget === 'calculate') {
        this.errorMessage = msg;
      } else {
        this.portfolioError = msg;
      }
    };

    if (this.selectedTickers.length === 0) {
      setError('Please select at least one mutual fund.');
      return false;
    }
    if (this.principal <= 0) {
      setError('Please enter an initial investment amount greater than 0.');
      return false;
    }
    if (this.years <= 0) {
      setError('Please enter a time horizon greater than 0 years.');
      return false;
    }
    return true;
  }

  onCalculate(): void {
    this.errorMessage = '';
    this.comparisonResult = null;

    if (!this.validateInputs('calculate')) return;

    this.isLoading = true;

    this.predictionService.compare(this.selectedTickers, this.principal, this.years)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: ComparisonResponse) => {
          this.comparisonResult = data;
          this.isLoading = false;
        },
        error: () => {
          this.errorMessage = 'Calculation failed. Please try again.';
          this.isLoading = false;
        }
      });
  }

  getHighestFutureValue(): number {
    if (!this.comparisonResult) return 0;
    let highest = 0;
    for (const result of this.comparisonResult.results) {
      if (result.prediction && result.prediction.futureValue > highest) {
        highest = result.prediction.futureValue;
      }
    }
    return highest;
  }

  onOptimize(): void {
    this.portfolioError = '';
    this.portfolioResult = null;

    if (!this.validateInputs('portfolio')) return;

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
}
