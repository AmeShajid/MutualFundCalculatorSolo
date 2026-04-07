import { Component, ViewChild, ElementRef } from '@angular/core';
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
  returnPercent: number;
}

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.css']
})
export class GameComponent {
  // Expose Math to template
  Math = Math;

  // Game phases
  gamePhase: 'start' | 'playing' | 'results' = 'start';

  // Player state
  invested = 10000;
  cash = 0;
  currentRound = 0;
  history: RoundSnapshot[] = [];

  // Transition state
  isTransitioning = false;
  lastReturnAmount = 0;

  // Buy-and-hold tracking
  buyAndHoldValues: number[] = [];

  // Canvas reference
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;

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
    this.isTransitioning = false;
    this.lastReturnAmount = 0;
    this.precomputeBuyAndHold();
    this.gamePhase = 'playing';
    setTimeout(() => this.drawChart(), 50);
  }

  // Precompute buy-and-hold values for all 10 years
  private precomputeBuyAndHold(): void {
    this.buyAndHoldValues = [10000];
    let value = 10000;
    for (const round of this.rounds) {
      value *= (1 + round.returnPercent);
      this.buyAndHoldValues.push(value);
    }
  }

  // Get current round data
  get currentRoundData(): GameRound {
    return this.rounds[this.currentRound];
  }

  // Get total portfolio value
  get totalValue(): number {
    return this.invested + this.cash;
  }

  // Check if sell half should be disabled
  get canSellHalf(): boolean {
    return this.invested > 0;
  }

  // Get player value history for chart
  get playerValues(): number[] {
    const values = [10000];
    for (const snap of this.history) {
      values.push(snap.total);
    }
    return values;
  }

  // Buy-and-hold final value
  get buyAndHoldFinal(): number {
    return this.buyAndHoldValues[this.buyAndHoldValues.length - 1] || 10000;
  }

  // Did the player beat buy-and-hold?
  get didBeatMarket(): boolean {
    return this.totalValue > this.buyAndHoldFinal;
  }

  // Player's total return percentage
  get playerReturnPercent(): number {
    return ((this.totalValue - 10000) / 10000) * 100;
  }

  // Buy-and-hold return percentage
  get bahReturnPercent(): number {
    return ((this.buyAndHoldFinal - 10000) / 10000) * 100;
  }

  // Grade based on performance vs buy-and-hold
  get grade(): string {
    const diff = ((this.totalValue - this.buyAndHoldFinal) / this.buyAndHoldFinal) * 100;
    if (diff >= 20) return 'A+';
    if (diff >= 10) return 'A';
    if (diff >= -5) return 'B';
    if (diff >= -15) return 'C';
    if (diff >= -25) return 'D';
    return 'F';
  }

  // Grade color class
  get gradeColor(): string {
    const g = this.grade;
    if (g === 'A+' || g === 'A') return 'grade-green';
    if (g === 'B') return 'grade-blue';
    if (g === 'C') return 'grade-yellow';
    return 'grade-red';
  }

  // Fun title based on play style
  get investorTitle(): string {
    const holds = this.history.filter(h => h.action === 'Stay Invested').length;
    const sells = this.history.filter(h => h.action === 'Sell Half').length;
    const allins = this.history.filter(h => h.action === 'Go All In').length;

    if (holds >= 7) return 'The Patient Investor';
    if (sells >= 5) return 'The Panic Seller';
    if (allins >= 5) return 'The Bold Trader';
    if (holds >= 4 && allins >= 3) return 'The Calculated Risk-Taker';
    if (sells >= 3 && allins >= 3) return 'The Market Timer';
    return 'The Balanced Strategist';
  }

  // Lesson message
  get lessonMessage(): string {
    if (this.didBeatMarket) {
      return 'Impressive — you outperformed buy-and-hold! But remember, past results don\'t guarantee future performance. Consistency usually wins long-term.';
    }
    return 'Staying invested through volatility usually wins. The market rewards patience — not timing.';
  }

  // Take an action for the current round
  takeAction(action: 'stay' | 'sell' | 'allin'): void {
    if (this.isTransitioning) return;

    const round = this.rounds[this.currentRound];
    const valueBefore = this.invested + this.cash;

    // Step 1: Apply action FIRST
    switch (action) {
      case 'stay':
        break;
      case 'sell':
        const halfInvested = this.invested * 0.5;
        this.cash += halfInvested;
        this.invested -= halfInvested;
        break;
      case 'allin':
        this.invested += this.cash + 1000;
        this.cash = 0;
        break;
    }

    // Step 2: Apply market return to invested only
    this.invested *= (1 + round.returnPercent);

    // Calculate how much the portfolio changed
    this.lastReturnAmount = (this.invested + this.cash) - valueBefore;

    // Step 3: Record snapshot
    const actionLabel = action === 'stay' ? 'Stay Invested' : action === 'sell' ? 'Sell Half' : 'Go All In';
    this.history.push({
      year: round.year,
      action: actionLabel,
      cash: this.cash,
      invested: this.invested,
      total: this.invested + this.cash,
      returnPercent: round.returnPercent
    });

    // Step 4: Update chart
    this.drawChart();

    // Step 5: Transition to next round or results
    this.isTransitioning = true;
    setTimeout(() => {
      this.isTransitioning = false;
      if (this.currentRound < this.rounds.length - 1) {
        this.currentRound++;
      } else {
        this.gamePhase = 'results';
      }
    }, 600);
  }

  // Draw the mini chart on canvas
  private drawChart(): void {
    const canvas = this.chartCanvas?.nativeElement;
    if (!canvas) return;

    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.getBoundingClientRect();
    canvas.width = rect.width * dpr;
    canvas.height = rect.height * dpr;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.scale(dpr, dpr);

    const w = rect.width;
    const h = rect.height;
    const pad = { top: 12, right: 12, bottom: 24, left: 12 };
    const chartW = w - pad.left - pad.right;
    const chartH = h - pad.top - pad.bottom;

    ctx.clearRect(0, 0, w, h);

    const playerVals = this.playerValues;
    const bahVals = this.buyAndHoldValues;

    const allVals = [...playerVals, ...bahVals.slice(0, playerVals.length)];
    const yMin = Math.min(...allVals) * 0.9;
    const yMax = Math.max(...allVals) * 1.1;

    const totalPoints = 11;
    const pointsToShow = playerVals.length;

    const xScale = (i: number) => pad.left + (i / (totalPoints - 1)) * chartW;
    const yScale = (v: number) => pad.top + chartH - ((v - yMin) / (yMax - yMin)) * chartH;

    // Grid lines
    ctx.strokeStyle = 'rgba(115, 153, 198, 0.08)';
    ctx.lineWidth = 1;
    for (let i = 0; i < 4; i++) {
      const y = pad.top + (chartH / 3) * i;
      ctx.beginPath();
      ctx.moveTo(pad.left, y);
      ctx.lineTo(w - pad.right, y);
      ctx.stroke();
    }

    // Buy-and-hold line (dashed)
    ctx.strokeStyle = 'rgba(255, 255, 255, 0.25)';
    ctx.lineWidth = 1.5;
    ctx.setLineDash([4, 4]);
    ctx.beginPath();
    for (let i = 0; i < pointsToShow; i++) {
      const x = xScale(i);
      const y = yScale(bahVals[i]);
      if (i === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    }
    ctx.stroke();
    ctx.setLineDash([]);

    // Player line (solid)
    ctx.strokeStyle = '#7399C6';
    ctx.lineWidth = 2.5;
    ctx.beginPath();
    for (let i = 0; i < pointsToShow; i++) {
      const x = xScale(i);
      const y = yScale(playerVals[i]);
      if (i === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    }
    ctx.stroke();

    // Glowing dot at last player point
    if (pointsToShow > 1) {
      const lastX = xScale(pointsToShow - 1);
      const lastY = yScale(playerVals[pointsToShow - 1]);
      ctx.beginPath();
      ctx.arc(lastX, lastY, 6, 0, Math.PI * 2);
      ctx.fillStyle = 'rgba(115, 153, 198, 0.3)';
      ctx.fill();
      ctx.beginPath();
      ctx.arc(lastX, lastY, 3, 0, Math.PI * 2);
      ctx.fillStyle = '#7399C6';
      ctx.fill();
    }

    // Year labels
    ctx.fillStyle = 'rgba(255, 255, 255, 0.25)';
    ctx.font = '10px "DM Mono", monospace';
    ctx.textAlign = 'center';
    ctx.fillText('Start', xScale(0), h - 4);
    for (let i = 0; i < this.history.length; i++) {
      ctx.fillText(String(this.history[i].year), xScale(i + 1), h - 4);
    }
  }
}
