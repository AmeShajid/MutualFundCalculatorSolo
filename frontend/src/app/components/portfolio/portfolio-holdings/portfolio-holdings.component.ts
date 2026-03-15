import { Component, Input, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PortfolioRecommendation } from '../../../models/portfolio-recommendation.model';

@Component({
  selector: 'app-portfolio-holdings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './portfolio-holdings.component.html',
  styleUrls: ['./portfolio-holdings.component.css']
})
export class PortfolioHoldingsComponent implements OnChanges {

  @Input() recommendation: PortfolioRecommendation | null = null;
  @Input() fundColors: Map<string, string> = new Map();

  totalProjected: number = 0;

  ngOnChanges(): void {
    if (this.recommendation) {
      this.totalProjected = this.recommendation.allocations.reduce(
        (sum, a) => sum + a.projectedValue, 0
      );
    } else {
      this.totalProjected = 0;
    }
  }

  getColor(ticker: string): string {
    return this.fundColors.get(ticker) || '#999';
  }
}
