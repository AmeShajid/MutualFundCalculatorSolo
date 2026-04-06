import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { Fund } from '../../models/fund.model';
import { FundService } from '../../services/fund.service';
import { PredictionService } from '../../services/prediction.service';
import { SimulationControlsComponent, SimulationParams } from './simulation-controls/simulation-controls.component';
import { SimulationChartComponent } from './simulation-chart/simulation-chart.component';
import { SimulationStatsComponent } from './simulation-stats/simulation-stats.component';
import { SimulationDetailsComponent } from './simulation-details/simulation-details.component';

// Shape of the percentile bands per year
export interface PercentileBands {
  p10: number[];
  p25: number[];
  p50: number[];
  p75: number[];
  p90: number[];
  capm: number[];
}

// All computed results passed to child components
export interface SimulationResults {
  bands: PercentileBands;
  years: number;
  principal: number;
  ticker: string;
  simulations: number;
  // Stats
  pessimisticValue: number;
  expectedValue: number;
  optimisticValue: number;
  // Details
  beta: number;
  volatility: number;
  capmFutureValue: number;
  expectedReturn: number;
  worstSim: number;
  bestSim: number;
}

@Component({
  selector: 'app-risk-analysis',
  standalone: true,
  imports: [CommonModule, SimulationControlsComponent, SimulationChartComponent, SimulationStatsComponent, SimulationDetailsComponent],
  templateUrl: './risk-analysis.component.html',
  styleUrls: ['./risk-analysis.component.css']
})
export class RiskAnalysisComponent implements OnInit, OnDestroy {
  // Fund list for the dropdown
  funds: Fund[] = [];
  // Whether simulation is currently running
  isRunning = false;
  // Simulation results (null until first run)
  results: SimulationResults | null = null;
  // Cleanup subject
  private destroy$ = new Subject<void>();

  constructor(
    private fundService: FundService,
    private predictionService: PredictionService
  ) {}

  ngOnInit(): void {
    // Load fund list from backend
    this.fundService.getFunds()
      .pipe(takeUntil(this.destroy$))
      .subscribe(funds => this.funds = funds);
  }

  // Called when user clicks "Run Simulation" in controls
  onRunSimulation(params: SimulationParams): void {
    this.isRunning = true;

    // Call backend to get beta and expected return for the selected fund
    this.predictionService.predict(params.ticker, params.principal, params.years)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (prediction) => {
          // Use setTimeout to let the UI update to "Simulating..." before heavy computation
          setTimeout(() => {
            const results = this.runMonteCarlo(
              params.principal,
              params.years,
              params.simulations,
              params.ticker,
              prediction.beta,
              prediction.expectedReturn,
              prediction.riskFreeRate
            );
            this.results = results;
            this.isRunning = false;
          }, 50);
        },
        error: (err) => {
          console.error('Failed to get prediction data:', err);
          this.isRunning = false;
        }
      });
  }

  // Core Monte Carlo simulation — runs entirely in the browser
  private runMonteCarlo(
    principal: number,
    years: number,
    numSims: number,
    ticker: string,
    beta: number,
    expectedReturn: number,
    riskFreeRate: number
  ): SimulationResults {
    // Calculate fund volatility from beta and market volatility (~15%)
    const marketVolatility = 0.15;
    const fundVolatility = beta * marketVolatility;

    // CAPM rate (same formula as backend)
    const capmRate = riskFreeRate + beta * (expectedReturn - riskFreeRate);

    // Store all paths: allPaths[simIndex][yearIndex] = value
    const allPaths: number[][] = [];
    const finalValues: number[] = [];

    // Run each simulation
    for (let s = 0; s < numSims; s++) {
      const path: number[] = [principal];
      let value = principal;

      for (let y = 1; y <= years; y++) {
        // Random annual return with volatility
        const randomReturn = capmRate + fundVolatility * this.randNormal();
        value = value * Math.exp(randomReturn);
        // Can't go below zero
        if (value < 0) value = 0;
        path.push(value);
      }

      allPaths.push(path);
      finalValues.push(value);
    }

    // Extract percentile bands for each year
    const p10: number[] = [];
    const p25: number[] = [];
    const p50: number[] = [];
    const p75: number[] = [];
    const p90: number[] = [];
    const capm: number[] = [];

    for (let y = 0; y <= years; y++) {
      // Collect all simulation values for this year
      const yearValues = allPaths.map(path => path[y]);
      yearValues.sort((a, b) => a - b);

      p10.push(this.percentile(yearValues, 0.10));
      p25.push(this.percentile(yearValues, 0.25));
      p50.push(this.percentile(yearValues, 0.50));
      p75.push(this.percentile(yearValues, 0.75));
      p90.push(this.percentile(yearValues, 0.90));

      // CAPM deterministic line for comparison
      capm.push(principal * Math.exp(capmRate * y));
    }

    // Sort final values for stats
    finalValues.sort((a, b) => a - b);

    return {
      bands: { p10, p25, p50, p75, p90, capm },
      years,
      principal,
      ticker,
      simulations: numSims,
      pessimisticValue: this.percentile(finalValues, 0.10),
      expectedValue: this.percentile(finalValues, 0.50),
      optimisticValue: this.percentile(finalValues, 0.90),
      beta,
      volatility: fundVolatility,
      capmFutureValue: principal * Math.exp(capmRate * years),
      expectedReturn,
      worstSim: finalValues[0],
      bestSim: finalValues[finalValues.length - 1]
    };
  }

  // Box-Muller transform: generates a random number from normal distribution (mean=0, sd=1)
  private randNormal(): number {
    let u = 0, v = 0;
    while (u === 0) u = Math.random();
    while (v === 0) v = Math.random();
    return Math.sqrt(-2.0 * Math.log(u)) * Math.cos(2.0 * Math.PI * v);
  }

  // Returns the value at a given percentile from a sorted array
  private percentile(sorted: number[], p: number): number {
    const index = Math.floor(p * sorted.length);
    return sorted[Math.min(index, sorted.length - 1)];
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
