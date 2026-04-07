import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// Data for each round of the game
export interface GameRound {
  year: number;
  headline: string;
  context: string;
  returnPercent: number;
}

// Snapshot after each round
export interface RoundSnapshot {
  year: number;
  action: string;
  cash: number;
  invested: number;
  total: number;
}

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.css']
})
export class GameComponent {
  // Game phases
  gamePhase: 'start' | 'playing' | 'results' = 'start';

  // Player state
  invested = 10000;
  cash = 0;
  currentRound = 0;
  history: RoundSnapshot[] = [];

  // 10 rounds of real S&P 500 data
  rounds: GameRound[] = [
    { year: 2014, headline: 'Bull market roars, oil prices crash', context: 'Markets climb despite global concerns', returnPercent: 0.114 },
    { year: 2015, headline: 'China fears spark global selloff', context: 'First rate hike in nearly a decade', returnPercent: 0.007 },
    { year: 2016, headline: 'Brexit vote shocks the world', context: 'Markets tumble then recover sharply', returnPercent: 0.095 },
    { year: 2017, headline: 'Tech stocks explode, crypto mania', context: 'Longest bull run in history continues', returnPercent: 0.194 },
    { year: 2018, headline: 'Trade war and rate hike panic', context: 'Worst December since the Great Depression', returnPercent: -0.062 },
    { year: 2019, headline: 'Fed reverses, markets soar', context: 'Rate cuts fuel a massive rally', returnPercent: 0.289 },
    { year: 2020, headline: 'Pandemic crashes markets 34%', context: 'Fastest crash and recovery in history', returnPercent: 0.163 },
    { year: 2021, headline: 'Stimulus rally, meme stock frenzy', context: 'Everything goes up, inflation brewing', returnPercent: 0.269 },
    { year: 2022, headline: 'Inflation spikes, markets plunge', context: 'Most aggressive rate hikes in 40 years', returnPercent: -0.194 },
    { year: 2023, headline: 'AI boom, Magnificent 7 rally', context: 'Tech giants drive recovery', returnPercent: 0.242 }
  ];

  // Start the game
  startGame(): void {
    this.invested = 10000;
    this.cash = 0;
    this.currentRound = 0;
    this.history = [];
    this.gamePhase = 'playing';
  }

  // Get current round data
  get currentRoundData(): GameRound {
    return this.rounds[this.currentRound];
  }

  // Get total portfolio value
  get totalValue(): number {
    return this.invested + this.cash;
  }
}
