import { Component, Input, Output, EventEmitter, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Fund } from '../../../models/fund.model';

export interface SelectedFund {
  symbol: string;
  name: string;
  color: string;
}

@Component({
  selector: 'app-portfolio-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './portfolio-sidebar.component.html',
  styleUrls: ['./portfolio-sidebar.component.css']
})
export class PortfolioSidebarComponent {

  @Input() funds: Fund[] = [];
  @Input() selectedFunds: SelectedFund[] = [];
  @Input() riskTolerance: string = 'moderate';
  @Input() principal: number = 10000;
  @Input() years: number = 10;
  @Input() isGenerating: boolean = false;

  @Output() fundAdded = new EventEmitter<Fund>();
  @Output() fundRemoved = new EventEmitter<string>();
  @Output() riskToleranceChange = new EventEmitter<string>();
  @Output() principalChange = new EventEmitter<number>();
  @Output() yearsChange = new EventEmitter<number>();
  @Output() generate = new EventEmitter<void>();

  searchQuery: string = '';
  showDropdown: boolean = false;

  constructor(private elementRef: ElementRef) {}

  get filteredFunds(): Fund[] {
    if (!this.searchQuery.trim()) return this.funds;
    const q = this.searchQuery.toLowerCase();
    return this.funds.filter(f =>
      f.name.toLowerCase().includes(q) || f.symbol.toLowerCase().includes(q)
    );
  }

  get availableFunds(): Fund[] {
    const selectedSymbols = new Set(this.selectedFunds.map(f => f.symbol));
    return this.filteredFunds.filter(f => !selectedSymbols.has(f.symbol));
  }

  isSelected(symbol: string): boolean {
    return this.selectedFunds.some(f => f.symbol === symbol);
  }

  onSearchFocus(): void {
    this.showDropdown = true;
  }

  onAddFund(fund: Fund): void {
    if (this.selectedFunds.length >= 5) return;
    if (this.isSelected(fund.symbol)) return;
    this.fundAdded.emit(fund);
    this.searchQuery = '';
  }

  onRemoveFund(symbol: string): void {
    this.fundRemoved.emit(symbol);
  }

  onRiskChange(risk: string): void {
    this.riskToleranceChange.emit(risk);
  }

  onPrincipalChange(value: number): void {
    this.principalChange.emit(value);
  }

  onYearsChange(value: number): void {
    this.yearsChange.emit(value);
  }

  onGenerate(): void {
    this.generate.emit();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.showDropdown = false;
    }
  }
}
