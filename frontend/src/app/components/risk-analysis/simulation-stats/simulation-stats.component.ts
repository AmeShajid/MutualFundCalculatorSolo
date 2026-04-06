import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SimulationResults } from '../risk-analysis.component';

@Component({
  selector: 'app-simulation-stats',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './simulation-stats.component.html',
  styleUrls: ['./simulation-stats.component.css']
})
export class SimulationStatsComponent {
  @Input() results!: SimulationResults;

  // Calculate percentage gain/loss vs principal
  percentGain(value: number): number {
    return ((value - this.results.principal) / this.results.principal) * 100;
  }

  // Format as +XX.X% or -XX.X%
  formatPercent(value: number): string {
    const pct = this.percentGain(value);
    return (pct >= 0 ? '+' : '') + pct.toFixed(1) + '%';
  }
}
