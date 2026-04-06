import { Component, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './hero.component.html',
  styleUrls: ['./hero.component.css']
})
export class HeroComponent implements AfterViewInit {
  // Reference to the counting stat element
  @ViewChild('statFunds') statFundsEl!: ElementRef;
  // Reference to the chart dollar amount
  @ViewChild('chartAmount') chartAmountEl!: ElementRef;

  ngAfterViewInit(): void {
    // Animate the "25+" counter from 0 to 25 over 1.5 seconds
    this.animateCount(this.statFundsEl.nativeElement, 25, 1500, '+');
    // Animate the chart dollar amount from 0 to 16470, starts after chart draws
    setTimeout(() => {
      this.animateDollar(this.chartAmountEl.nativeElement, 16470, 1500);
    }, 500);
  }

  // Counts up from 0 to target over duration ms
  private animateCount(el: HTMLElement, target: number, duration: number, suffix: string): void {
    const start = performance.now();
    const step = (timestamp: number) => {
      const progress = Math.min((timestamp - start) / duration, 1);
      el.textContent = Math.floor(progress * target) + suffix;
      if (progress < 1) requestAnimationFrame(step);
    };
    requestAnimationFrame(step);
  }

  // Animates dollar amount with comma formatting
  private animateDollar(el: HTMLElement, target: number, duration: number): void {
    const start = performance.now();
    const step = (timestamp: number) => {
      const progress = Math.min((timestamp - start) / duration, 1);
      const value = Math.floor(progress * target);
      el.textContent = '$' + value.toLocaleString();
      if (progress < 1) requestAnimationFrame(step);
    };
    requestAnimationFrame(step);
  }
}
