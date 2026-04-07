import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tech-stack',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tech-stack.component.html',
  styleUrls: ['./tech-stack.component.css']
})
export class TechStackComponent {
  expandedCard: string | null = null;

  descriptions: Record<string, string> = {
    'Spring Boot': 'Powers the REST API that serves fund data and runs predictions.',
    'Angular 19': 'Renders the entire frontend as a single-page application.',
    'Gemini': 'Analyzes your portfolio and suggests optimized allocations.',
    'Newton Analytics': 'Provides live beta coefficients for each fund.',
    'Yahoo Finance': 'Supplies 1-year historical prices for return calculations.',
    'REST APIs': 'Connects the Angular frontend to the Spring Boot backend.',
    'Maven': 'Manages Java dependencies and builds the backend JAR.',
    'Gemini API': 'Handles AI chat and portfolio optimization requests.'
  };

  toggleCard(name: string): void {
    this.expandedCard = this.expandedCard === name ? null : name;
  }
}
