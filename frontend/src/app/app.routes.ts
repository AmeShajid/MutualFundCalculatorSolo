import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { CalculatorComponent } from './components/calculator/calculator.component';
import { PortfolioComponent } from './components/portfolio/portfolio.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'calculator', component: CalculatorComponent },
  { path: 'portfolio', component: PortfolioComponent },
  { path: '**', redirectTo: '' }
];
