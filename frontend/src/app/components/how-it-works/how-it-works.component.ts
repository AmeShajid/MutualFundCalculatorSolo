import { Component } from '@angular/core';
import { InfoCardsComponent } from '../home/info-cards/info-cards.component';

@Component({
  selector: 'app-how-it-works',
  standalone: true,
  imports: [InfoCardsComponent],
  template: `
    <div class="page">
      <app-info-cards></app-info-cards>
    </div>
  `,
  styles: [`
    .page {
      min-height: calc(100vh - 64px);
      display: flex;
      flex-direction: column;
      align-items: center;
    }
  `]
})
export class HowItWorksComponent {}
