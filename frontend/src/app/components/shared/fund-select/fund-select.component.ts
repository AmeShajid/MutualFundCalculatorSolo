import { Component, Input, Output, EventEmitter, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Fund } from '../../../models/fund.model';

@Component({
  selector: 'app-fund-select',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './fund-select.component.html',
  styleUrls: ['./fund-select.component.css']
})
export class FundSelectComponent {

  @Input() funds: Fund[] = [];
  @Input() maxSelections: number = 5;
  @Input() selectedTickers: string[] = [];
  @Output() selectedTickersChange = new EventEmitter<string[]>();

  dropdownOpen: boolean = false;
  searchText: string = '';

  get filteredFunds(): Fund[] {
    if (!this.searchText) {
      return this.funds;
    }
    const search = this.searchText.toLowerCase();
    return this.funds.filter(f =>
      f.name.toLowerCase().includes(search) || f.symbol.toLowerCase().includes(search)
    );
  }

  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
    if (!this.dropdownOpen) {
      this.searchText = '';
    }
  }

  isSelected(symbol: string): boolean {
    return this.selectedTickers.includes(symbol);
  }

  isDisabled(symbol: string): boolean {
    return this.selectedTickers.length >= this.maxSelections && !this.isSelected(symbol);
  }

  toggleFund(symbol: string): void {
    let updated: string[];
    if (this.isSelected(symbol)) {
      updated = this.selectedTickers.filter(t => t !== symbol);
    } else if (this.selectedTickers.length < this.maxSelections) {
      updated = [...this.selectedTickers, symbol];
    } else {
      return;
    }
    this.selectedTickers = updated;
    this.selectedTickersChange.emit(updated);
  }

  getDropdownLabel(): string {
    if (this.selectedTickers.length === 0) {
      return 'Select Funds';
    } else if (this.selectedTickers.length === 1) {
      const fund = this.funds.find(f => f.symbol === this.selectedTickers[0]);
      return fund ? fund.name : this.selectedTickers[0];
    } else {
      return this.selectedTickers.length + ' funds selected';
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.multi-select-container')) {
      this.dropdownOpen = false;
      this.searchText = '';
    }
  }
}
