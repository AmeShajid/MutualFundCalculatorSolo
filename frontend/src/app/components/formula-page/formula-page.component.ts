import { Component } from '@angular/core';
import { FormulaComponent } from '../home/formula/formula.component';

@Component({
  selector: 'app-formula-page',
  standalone: true,
  imports: [FormulaComponent],
  template: `
    <div class="page">
      <app-formula></app-formula>
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
export class FormulaPageComponent {}
