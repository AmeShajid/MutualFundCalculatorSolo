import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { Fund } from '../../models/fund.model';
import { FundService } from '../../services/fund.service';
import { SimulationControlsComponent, SimulationParams } from './simulation-controls/simulation-controls.component';

@Component({
  selector: 'app-risk-analysis',
  standalone: true,
  imports: [CommonModule, SimulationControlsComponent],
  templateUrl: './risk-analysis.component.html',
  styleUrls: ['./risk-analysis.component.css']
})
export class RiskAnalysisComponent implements OnInit, OnDestroy {
  // Fund list for the dropdown
  funds: Fund[] = [];
  // Whether simulation is currently running
  isRunning = false;
  // Cleanup subject
  private destroy$ = new Subject<void>();

  constructor(private fundService: FundService) {}

  ngOnInit(): void {
    // Load fund list from backend
    this.fundService.getFunds()
      .pipe(takeUntil(this.destroy$))
      .subscribe(funds => this.funds = funds);
  }

  // Called when user clicks "Run Simulation" in controls
  onRunSimulation(params: SimulationParams): void {
    console.log('Simulation params:', params);
    // Simulation engine will be added in Milestone 3
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
