import { Component, Input, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PortfolioRecommendation } from '../../../models/portfolio-recommendation.model';

@Component({
  selector: 'app-portfolio-summary',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './portfolio-summary.component.html',
  styleUrls: ['./portfolio-summary.component.css']
})
export class PortfolioSummaryComponent implements OnChanges {

  @Input() recommendation: PortfolioRecommendation | null = null;
  @Input() principal: number = 0;
  @Input() years: number = 0;

  totalProjected: number = 0;
  totalGain: number = 0;
  returnPercent: number = 0;

  ngOnChanges(): void {
    if (this.recommendation) {
      this.totalProjected = this.recommendation.allocations.reduce(
        (sum, a) => sum + a.projectedValue, 0
      );
      this.totalGain = this.totalProjected - this.principal;
      this.returnPercent = this.principal > 0
        ? (this.totalGain / this.principal) * 100
        : 0;
    } else {
      this.totalProjected = 0;
      this.totalGain = 0;
      this.returnPercent = 0;
    }
  }
}
