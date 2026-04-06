import { Component, Input, OnChanges, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SimulationResults } from '../risk-analysis.component';

@Component({
  selector: 'app-simulation-chart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './simulation-chart.component.html',
  styleUrls: ['./simulation-chart.component.css']
})
export class SimulationChartComponent implements OnChanges, AfterViewInit {
  // Simulation results from parent
  @Input() results: SimulationResults | null = null;
  // Canvas element reference
  @ViewChild('mcChart') canvasRef!: ElementRef<HTMLCanvasElement>;

  private viewReady = false;

  // Dynamic subtitle text
  get subtitle(): string {
    if (!this.results) return 'Select a fund and run the simulation';
    const r = this.results;
    return `${r.ticker} · $${r.principal.toLocaleString()} · ${r.years} years · ${r.simulations} simulations`;
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    if (this.results) this.drawChart();
  }

  ngOnChanges(): void {
    if (this.viewReady && this.results) {
      // Small delay to ensure canvas is in DOM
      setTimeout(() => this.drawChart(), 0);
    }
  }

  private drawChart(): void {
    if (!this.results || !this.canvasRef) return;

    const canvas = this.canvasRef.nativeElement;
    const container = canvas.parentElement!;
    const dpr = window.devicePixelRatio || 1;

    // Set canvas size for retina
    const width = container.clientWidth;
    const height = container.clientHeight;
    canvas.width = width * dpr;
    canvas.height = height * dpr;
    canvas.style.width = width + 'px';
    canvas.style.height = height + 'px';

    const ctx = canvas.getContext('2d')!;
    ctx.scale(dpr, dpr);

    const { bands, years, principal } = this.results;

    // Chart padding
    const padLeft = 70;
    const padRight = 50;
    const padTop = 20;
    const padBottom = 40;
    const chartWidth = width - padLeft - padRight;
    const chartHeight = height - padTop - padBottom;

    // Find min/max across all bands
    const allValues = [...bands.p10, ...bands.p90, ...bands.capm];
    const minVal = Math.min(...allValues) * 0.9;
    const maxVal = Math.max(...allValues) * 1.05;

    // Scale functions
    const xPos = (y: number) => padLeft + (y / years) * chartWidth;
    const yPos = (v: number) => padTop + chartHeight - ((v - minVal) / (maxVal - minVal)) * chartHeight;

    // Clear
    ctx.clearRect(0, 0, width, height);

    // 1. Grid lines + Y-axis labels
    ctx.strokeStyle = 'rgba(115,153,198,0.06)';
    ctx.lineWidth = 1;
    ctx.font = '11px DM Mono, monospace';
    ctx.fillStyle = 'rgba(255,255,255,0.25)';
    ctx.textAlign = 'right';

    for (let i = 0; i <= 5; i++) {
      const val = minVal + (maxVal - minVal) * (i / 5);
      const y = yPos(val);
      ctx.beginPath();
      ctx.moveTo(padLeft, y);
      ctx.lineTo(width - padRight, y);
      ctx.stroke();
      // Y-axis label
      const label = val >= 1000000 ? '$' + (val / 1000000).toFixed(1) + 'M' : '$' + Math.round(val / 1000) + 'k';
      ctx.fillText(label, padLeft - 10, y + 4);
    }

    // 2. X-axis year labels
    ctx.fillStyle = 'rgba(255,255,255,0.25)';
    ctx.textAlign = 'center';
    const currentYear = new Date().getFullYear();
    const step = years <= 10 ? 1 : years <= 20 ? 2 : 5;

    for (let y = 0; y <= years; y += step) {
      ctx.fillText(String(currentYear + y), xPos(y), height - 10);
    }
    // Always label final year
    if (years % step !== 0) {
      ctx.fillText(String(currentYear + years), xPos(years), height - 10);
    }

    // 3. Outer band (p10-p90)
    ctx.beginPath();
    for (let y = 0; y <= years; y++) ctx.lineTo(xPos(y), yPos(bands.p90[y]));
    for (let y = years; y >= 0; y--) ctx.lineTo(xPos(y), yPos(bands.p10[y]));
    ctx.closePath();
    ctx.fillStyle = 'rgba(115,153,198,0.1)';
    ctx.fill();

    // 4. Inner band (p25-p75)
    ctx.beginPath();
    for (let y = 0; y <= years; y++) ctx.lineTo(xPos(y), yPos(bands.p75[y]));
    for (let y = years; y >= 0; y--) ctx.lineTo(xPos(y), yPos(bands.p25[y]));
    ctx.closePath();
    ctx.fillStyle = 'rgba(115,153,198,0.15)';
    ctx.fill();

    // 5. CAPM dashed line
    ctx.beginPath();
    ctx.setLineDash([6, 4]);
    ctx.strokeStyle = 'rgba(255,255,255,0.3)';
    ctx.lineWidth = 1.5;
    for (let y = 0; y <= years; y++) {
      if (y === 0) ctx.moveTo(xPos(y), yPos(bands.capm[y]));
      else ctx.lineTo(xPos(y), yPos(bands.capm[y]));
    }
    ctx.stroke();
    ctx.setLineDash([]);

    // 6. Median line (p50)
    ctx.beginPath();
    ctx.strokeStyle = '#7399C6';
    ctx.lineWidth = 2.5;
    ctx.lineJoin = 'round';
    ctx.lineCap = 'round';
    for (let y = 0; y <= years; y++) {
      if (y === 0) ctx.moveTo(xPos(y), yPos(bands.p50[y]));
      else ctx.lineTo(xPos(y), yPos(bands.p50[y]));
    }
    ctx.stroke();

    // 7. End dot on median
    const endX = xPos(years);
    const endY = yPos(bands.p50[years]);
    // Glow ring
    ctx.beginPath();
    ctx.arc(endX, endY, 8, 0, Math.PI * 2);
    ctx.strokeStyle = 'rgba(115,153,198,0.3)';
    ctx.lineWidth = 2;
    ctx.stroke();
    // Solid dot
    ctx.beginPath();
    ctx.arc(endX, endY, 5, 0, Math.PI * 2);
    ctx.fillStyle = '#7399C6';
    ctx.fill();

    // 8. End value labels
    ctx.font = '500 12px DM Mono, monospace';
    ctx.textAlign = 'left';
    const labelX = endX + 14;

    // 90th percentile label
    ctx.fillStyle = 'rgba(255,255,255,0.3)';
    ctx.fillText(this.formatShort(bands.p90[years]), labelX, yPos(bands.p90[years]) + 4);
    // Median label
    ctx.fillStyle = '#7399C6';
    ctx.fillText(this.formatShort(bands.p50[years]), labelX, endY + 4);
    // 10th percentile label
    ctx.fillStyle = 'rgba(255,255,255,0.3)';
    ctx.fillText(this.formatShort(bands.p10[years]), labelX, yPos(bands.p10[years]) + 4);
  }

  // Format dollar values as $XXk or $X.XM
  private formatShort(value: number): string {
    if (value >= 1000000) return '$' + (value / 1000000).toFixed(1) + 'M';
    return '$' + Math.round(value / 1000) + 'k';
  }
}
