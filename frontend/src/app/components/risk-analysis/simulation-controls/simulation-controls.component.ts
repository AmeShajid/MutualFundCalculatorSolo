import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Fund } from '../../../models/fund.model';

// Shape of the data emitted when user clicks "Run Simulation"
export interface SimulationParams {
  ticker: string;
  principal: number;
  years: number;
  simulations: number;
}

@Component({
  selector: 'app-simulation-controls',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './simulation-controls.component.html',
  styleUrls: ['./simulation-controls.component.css']
})
export class SimulationControlsComponent {
  // Fund list passed in from parent
  @Input() funds: Fund[] = [];
  // Whether simulation is currently running (disables button)
  @Input() isRunning = false;
  // Emits form data when user clicks Run
  @Output() runSimulation = new EventEmitter<SimulationParams>();

  // Form state
  selectedTicker = '';
  principal = 10000;
  years = 10;
  simulations = 500;

  // Emit the current form values to the parent
  onRun(): void {
    if (!this.selectedTicker || this.principal <= 0 || this.years <= 0) return;
    this.runSimulation.emit({
      ticker: this.selectedTicker,
      principal: this.principal,
      years: this.years,
      simulations: this.simulations
    });
  }
}
