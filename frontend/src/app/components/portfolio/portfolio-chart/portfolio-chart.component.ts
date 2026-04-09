import { Component, Input, OnChanges, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PortfolioRecommendation } from '../../../models/portfolio-recommendation.model';
import { FundAllocation } from '../../../models/fund-allocation.model';

interface ChartLine {
  ticker: string;
  color: string;
  points: string; // SVG polyline points
}

interface GridLine {
  y: number;
  label: string;
}

interface XTick {
  x: number;
  label: string;
}

interface YearData {
  year: number;
  funds: { ticker: string; color: string; value: number }[];
  total: number;
}

@Component({
  selector: 'app-portfolio-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './portfolio-chart.component.html',
  styleUrls: ['./portfolio-chart.component.css']
})
export class PortfolioChartComponent implements OnChanges {

  @Input() recommendation: PortfolioRecommendation | null = null;
  @Input() fundColors: Map<string, string> = new Map();
  @Input() principal: number = 0;
  @Input() years: number = 0;

  @ViewChild('chartSvg', { static: false }) chartSvgRef!: ElementRef<SVGSVGElement>;

  // Plot area bounds
  readonly xMin = 70;
  readonly xMax = 730;
  readonly yMin = 30;
  readonly yMax = 340;

  fundLines: ChartLine[] = [];
  totalLine: string = '';
  totalAreaPoints: string = '';
  gridLines: GridLine[] = [];
  xTicks: XTick[] = [];
  breakEvenY: number = 0;
  breakEvenLabel: string = '';
  endpointX: number = 0;
  endpointY: number = 0;
  endpointLabel: string = '';
  yMaxValue: number = 0;
  gradientId = 'totalGradient';

  legendItems: { ticker: string; color: string }[] = [];

  // Tooltip state
  yearlyData: YearData[] = [];
  hoveredYear: number | null = null;
  hoverLineX: number = 0;
  tooltipX: number = 0;
  tooltipY: number = 0;
  tooltipData: YearData | null = null;

  ngOnChanges(): void {
    if (!this.recommendation || !this.recommendation.allocations.length || this.years <= 0) {
      this.fundLines = [];
      this.totalLine = '';
      this.totalAreaPoints = '';
      this.gridLines = [];
      this.xTicks = [];
      this.legendItems = [];
      this.yearlyData = [];
      return;
    }

    this.computeChart();
  }

  onChartMouseMove(event: MouseEvent): void {
    if (!this.chartSvgRef || !this.yearlyData.length) return;

    const svg = this.chartSvgRef.nativeElement;
    const rect = svg.getBoundingClientRect();

    // Map mouse X to SVG coordinate space
    const svgWidth = 800; // viewBox width
    const mouseX = ((event.clientX - rect.left) / rect.width) * svgWidth;

    // Find the nearest year
    const yearFrac = ((mouseX - this.xMin) / (this.xMax - this.xMin)) * this.years;
    const nearestYear = Math.round(Math.max(0, Math.min(this.years, yearFrac)));

    if (nearestYear === this.hoveredYear) return;

    this.hoveredYear = nearestYear;
    this.tooltipData = this.yearlyData[nearestYear] || null;

    // Position the hover line
    this.hoverLineX = this.xMin + (nearestYear / this.years) * (this.xMax - this.xMin);

    // Position tooltip — offset to the right of the line, flip if near right edge
    const tooltipWidth = 180; // approximate
    if (this.hoverLineX > (this.xMax - tooltipWidth - 20)) {
      this.tooltipX = this.hoverLineX - tooltipWidth - 12;
    } else {
      this.tooltipX = this.hoverLineX + 12;
    }
    this.tooltipY = this.yMin + 10;
  }

  onChartMouseLeave(): void {
    this.hoveredYear = null;
    this.tooltipData = null;
  }

  private computeChart(): void {
    if (!this.recommendation) return;
    const allocs = this.recommendation.allocations;
    const years = this.years;

    // Compute fund values over time using FV = allocatedAmount * e^(expectedReturn * t)
    const fundData: { ticker: string; color: string; values: number[] }[] = [];
    const totalValues: number[] = new Array(years + 1).fill(0);

    for (const alloc of allocs) {
      const color = this.fundColors.get(alloc.ticker) || '#999';
      const values: number[] = [];
      for (let t = 0; t <= years; t++) {
        const fv = alloc.allocatedAmount * Math.exp(alloc.expectedReturn * t);
        values.push(fv);
        totalValues[t] += fv;
      }
      fundData.push({ ticker: alloc.ticker, color, values });
    }

    // Build yearly data for tooltips
    this.yearlyData = [];
    for (let t = 0; t <= years; t++) {
      this.yearlyData.push({
        year: t,
        funds: fundData.map(fd => ({
          ticker: fd.ticker,
          color: fd.color,
          value: fd.values[t]
        })),
        total: totalValues[t]
      });
    }

    // Determine y scale
    this.yMaxValue = Math.max(...totalValues) * 1.1;
    const yMinValue = 0;

    // Scale functions
    const scaleX = (t: number) =>
      this.xMin + (t / years) * (this.xMax - this.xMin);
    const scaleY = (v: number) =>
      this.yMax - ((v - yMinValue) / (this.yMaxValue - yMinValue)) * (this.yMax - this.yMin);

    // Fund lines
    this.fundLines = fundData.map(fd => ({
      ticker: fd.ticker,
      color: fd.color,
      points: fd.values.map((v, t) => `${scaleX(t)},${scaleY(v)}`).join(' ')
    }));

    // Total line
    const totalPoints = totalValues.map((v, t) => `${scaleX(t)},${scaleY(v)}`);
    this.totalLine = totalPoints.join(' ');

    // Area under total line
    this.totalAreaPoints = totalPoints.join(' ') +
      ` ${scaleX(years)},${this.yMax} ${scaleX(0)},${this.yMax}`;

    // Grid lines (5 lines)
    this.gridLines = [];
    const step = this.yMaxValue / 5;
    for (let i = 1; i <= 5; i++) {
      const val = step * i;
      this.gridLines.push({
        y: scaleY(val),
        label: this.formatDollar(val)
      });
    }

    // X ticks
    this.xTicks = [];
    const tickInterval = years > 10 ? 2 : 1;
    for (let t = 0; t <= years; t += tickInterval) {
      this.xTicks.push({
        x: scaleX(t),
        label: `${t}`
      });
    }

    // Break-even line
    this.breakEvenY = scaleY(this.principal);
    this.breakEvenLabel = this.formatDollar(this.principal);

    // Endpoint — position label to the left if near right edge
    const finalTotal = totalValues[years];
    this.endpointX = scaleX(years);
    this.endpointY = scaleY(finalTotal);
    this.endpointLabel = this.formatDollar(finalTotal);

    // Legend
    this.legendItems = fundData.map(fd => ({
      ticker: fd.ticker,
      color: fd.color
    }));

    // Reset hover
    this.hoveredYear = null;
    this.tooltipData = null;
  }

  formatDollar(value: number): string {
    if (value >= 1000000) return `$${(value / 1000000).toFixed(1)}M`;
    if (value >= 1000) return `$${(value / 1000).toFixed(0)}k`;
    return `$${value.toFixed(0)}`;
  }
}
