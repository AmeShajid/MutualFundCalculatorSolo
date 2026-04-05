import { Component, AfterViewInit, QueryList, ElementRef, ViewChildren } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeroComponent } from './hero/hero.component';
import { InfoCardsComponent } from './info-cards/info-cards.component';
import { FormulaComponent } from './formula/formula.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, HeroComponent, InfoCardsComponent, FormulaComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements AfterViewInit {
  // Grab all elements with the #revealEl template ref for scroll animation
  @ViewChildren('revealEl') revealElements!: QueryList<ElementRef>;

  ngAfterViewInit(): void {
    // Set up IntersectionObserver for scroll-reveal animations
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('visible');
        }
      });
    }, { threshold: 0.15 });

    // Observe all reveal elements
    this.revealElements.forEach(el => observer.observe(el.nativeElement));
  }
}
