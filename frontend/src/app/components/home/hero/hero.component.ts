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

  ngAfterViewInit(): void {
    // Animate the "25+" counter from 0 to 25 over 1.5 seconds
    this.animateCount(this.statFundsEl.nativeElement, 25, 1500);
  }

  // Counts up from 0 to target over duration ms using requestAnimationFrame
  private animateCount(el: HTMLElement, target: number, duration: number): void {
    const start = performance.now();

    const step = (timestamp: number) => {
      const progress = Math.min((timestamp - start) / duration, 1);
      el.textContent = Math.floor(progress * target) + '+';
      if (progress < 1) {
        requestAnimationFrame(step);
      }
    };

    requestAnimationFrame(step);
  }
}
