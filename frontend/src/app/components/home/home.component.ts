import { Component } from '@angular/core';
import { HeroComponent } from './hero/hero.component';
import { InfoCardsComponent } from './info-cards/info-cards.component';
import { FormulaComponent } from './formula/formula.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [HeroComponent, InfoCardsComponent, FormulaComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {}
