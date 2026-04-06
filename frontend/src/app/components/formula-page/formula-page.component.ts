import { Component } from '@angular/core';
import { FormulaComponent } from '../home/formula/formula.component';

@Component({
  selector: 'app-formula-page',
  standalone: true,
  imports: [FormulaComponent],
  templateUrl: './formula-page.component.html',
  styleUrls: ['./formula-page.component.css']
})
export class FormulaPageComponent {}
