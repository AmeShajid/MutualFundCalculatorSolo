import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { CalculatorComponent } from './components/calculator/calculator.component';
import { PortfolioComponent } from './components/portfolio/portfolio.component';
import { HowItWorksComponent } from './components/how-it-works/how-it-works.component';
import { FormulaPageComponent } from './components/formula-page/formula-page.component';
import { TechStackComponent } from './components/tech-stack/tech-stack.component';
import { RiskAnalysisComponent } from './components/risk-analysis/risk-analysis.component';
import { GameComponent } from './components/game/game.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'how-it-works', component: HowItWorksComponent },
  { path: 'formula', component: FormulaPageComponent },
  { path: 'tech-stack', component: TechStackComponent },
  { path: 'risk-analysis', component: RiskAnalysisComponent },
  { path: 'calculator', component: CalculatorComponent },
  { path: 'portfolio', component: PortfolioComponent },
  { path: 'game', component: GameComponent },
  { path: '**', redirectTo: '' }
];
