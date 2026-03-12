// Component decorator lets us define this class as an Angular component
import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Import our service that fetches the fund list from Spring Boot
import { FundService } from '../../services/fund.service';

// Import our service that sends prediction requests to Spring Boot
import { PredictionService } from '../../services/prediction.service';

// Import our interfaces so TypeScript knows the shape of our data
import { Fund } from '../../models/fund.model';
import { ComparisonResponse } from '../../models/comparison-response.model';

// @Component tells Angular this is a component and configures it
@Component({
  // selector is the HTML tag name we use to place this component
  selector: 'app-calculator',

  // templateUrl points to the HTML file for this component's UI
  templateUrl: './calculator.component.html',

  // styleUrls points to the CSS file for this component's styles
  styleUrls: ['./calculator.component.css'],

  // Import CommonModule for *ngIf/*ngFor and FormsModule for [(ngModel)]
  imports: [CommonModule, FormsModule]
})

// OnInit means this class has an ngOnInit method that runs on page load
export class CalculatorComponent implements OnInit {

  // This array will hold all the funds loaded from the backend dropdown
  funds: Fund[] = [];

  // This array holds the ticker symbols the user has selected
  selectedTickers: string[] = [];

  // This holds the initial investment amount the user types in
  principal: number = 0;

  // This holds the number of years the user types in
  years: number = 0;

  // This holds the comparison result returned from Spring Boot
  // It starts as null because we have no result yet
  comparisonResult: ComparisonResponse | null = null;

  // This tracks whether we are currently waiting for the backend to respond
  isLoading: boolean = false;

  // This holds any error message we want to show the user
  errorMessage: string = '';

  // Controls whether the multi-select dropdown is open or closed
  dropdownOpen: boolean = false;

  // Maximum number of funds that can be selected for comparison
  maxSelections: number = 5;

  // Angular injects both services automatically via the constructor
  constructor(
    // fundService will be used to load the dropdown list
    private fundService: FundService,
    // predictionService will be used when the user clicks Calculate
    private predictionService: PredictionService
  ) {}

  // ngOnInit runs automatically when the component first loads
  // This is where we load the fund list for the dropdown
  ngOnInit(): void {

    // Call our fund service to get the list of funds from Spring Boot
    // subscribe() means "when the data arrives, run this function"
    this.fundService.getFunds().subscribe({

      // next runs when data arrives successfully
      next: (data: Fund[]) => {
        // Store the fund list so the dropdown can display it
        this.funds = data;
      },

      // error runs if something goes wrong loading funds
      error: (err) => {
        // Show the user a friendly error message
        this.errorMessage = 'Could not load funds. Make sure the backend is running.';
      }
    });
  }

  // Toggles the multi-select dropdown open/closed
  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
  }

  // Checks if a fund is currently selected
  isSelected(symbol: string): boolean {
    return this.selectedTickers.includes(symbol);
  }

  // Checks if a fund's checkbox should be disabled (max reached and not already selected)
  isDisabled(symbol: string): boolean {
    return this.selectedTickers.length >= this.maxSelections && !this.isSelected(symbol);
  }

  // Toggles a fund's selection on/off when its checkbox is clicked
  toggleFund(symbol: string): void {
    if (this.isSelected(symbol)) {
      // Remove the fund from selections
      this.selectedTickers = this.selectedTickers.filter(t => t !== symbol);
    } else if (this.selectedTickers.length < this.maxSelections) {
      // Add the fund to selections
      this.selectedTickers.push(symbol);
    }
  }

  // Returns display text for the dropdown button
  getDropdownLabel(): string {
    if (this.selectedTickers.length === 0) {
      return 'Select Funds';
    } else if (this.selectedTickers.length === 1) {
      // Show the fund name for single selection
      const fund = this.funds.find(f => f.symbol === this.selectedTickers[0]);
      return fund ? fund.name : this.selectedTickers[0];
    } else {
      return this.selectedTickers.length + ' funds selected';
    }
  }

  // Closes the dropdown when the user clicks outside of it
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    const target = event.target as HTMLElement;
    // Check if the click was outside the dropdown container
    if (!target.closest('.multi-select-container')) {
      this.dropdownOpen = false;
    }
  }

  // This method runs when the user clicks the Calculate button
  onCalculate(): void {

    // Clear any previous error message before starting a new calculation
    this.errorMessage = '';

    // Clear any previous result so old data doesn't show while loading
    this.comparisonResult = null;

    // Validate that the user selected at least one fund
    if (this.selectedTickers.length === 0) {
      // Show an error and stop — do not call the backend
      this.errorMessage = 'Please select at least one mutual fund.';
      return;
    }

    // Validate that the principal is a positive number
    if (this.principal <= 0) {
      // Show an error and stop — do not call the backend
      this.errorMessage = 'Please enter an initial investment amount greater than 0.';
      return;
    }

    // Validate that the years value is a positive number
    if (this.years <= 0) {
      // Show an error and stop — do not call the backend
      this.errorMessage = 'Please enter a time horizon greater than 0 years.';
      return;
    }

    // Show the loading spinner since we're about to call the backend
    this.isLoading = true;

    // Call the comparison endpoint with all selected tickers
    // subscribe() means "when Spring Boot responds, run this function"
    this.predictionService.compare(this.selectedTickers, this.principal, this.years).subscribe({

      // next runs when Spring Boot returns results
      next: (data: ComparisonResponse) => {
        // Store the result so the HTML can display it
        this.comparisonResult = data;
        // Hide the loading spinner since we have our result
        this.isLoading = false;
      },

      // error runs if Spring Boot returns an error or is unreachable
      error: (err) => {
        // Show a friendly error message to the user
        this.errorMessage = 'Calculation failed. Please try again.';
        // Hide the loading spinner since we stopped waiting
        this.isLoading = false;
      }
    });
  }

  // Returns the highest future value among successful results for highlighting
  getHighestFutureValue(): number {
    if (!this.comparisonResult) return 0;
    let highest = 0;
    for (const result of this.comparisonResult.results) {
      if (result.prediction && result.prediction.futureValue > highest) {
        highest = result.prediction.futureValue;
      }
    }
    return highest;
  }
}
