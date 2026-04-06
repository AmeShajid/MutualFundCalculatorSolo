import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SimulationResults } from '../risk-analysis.component';

@Component({
  selector: 'app-simulation-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './simulation-details.component.html',
  styleUrls: ['./simulation-details.component.css']
})
export class SimulationDetailsComponent {
  @Input() results!: SimulationResults;
}
