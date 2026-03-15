import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComparisonResponse } from '../../../models/comparison-response.model';

@Component({
  selector: 'app-results-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './results-table.component.html',
  styleUrls: ['./results-table.component.css']
})
export class ResultsTableComponent {
  @Input() comparisonResult: ComparisonResponse | null = null;
  @Input() highlightValue: number = 0;
}
