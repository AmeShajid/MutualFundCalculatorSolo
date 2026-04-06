import { Component } from '@angular/core';
import { InfoCardsComponent } from '../home/info-cards/info-cards.component';

@Component({
  selector: 'app-how-it-works',
  standalone: true,
  imports: [InfoCardsComponent],
  templateUrl: './how-it-works.component.html',
  styleUrls: ['./how-it-works.component.css']
})
export class HowItWorksComponent {}
