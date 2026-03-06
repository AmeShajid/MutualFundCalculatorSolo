// Component decorator lets us define this class as an Angular component
import { Component, OnInit } from '@angular/core';

// Import our service that fetches the fund list from Spring Boot
import { FundService } from '../../services/fund.service';

// Import our service that sends prediction requests to Spring Boot
import { PredictionService } from '../../services/prediction.service';

// Import our interfaces so TypeScript knows the shape of our data
import { Fund } from '../../models/fund.model';
import { PredictionResponse } from '../../models/prediction-response.model';

// @Component tells Angular this is a component and configures it
@Component({
  // selector is the HTML tag name we use to place this component
  selector: 'app-calculator',

  // templateUrl points to the HTML file for this component's UI
  templateUrl: './calculator.component.html',

  // styleUrls points to the CSS file for this component's styles
  styleUrls: ['./calculator.component.css']
})

// OnInit means this class has an ngOnInit method that runs on page load
export class CalculatorComponent implements OnInit {

  // This array will hold all the funds loaded from the backend dropdown
  funds: Fund[] = [];

  // This holds the ticker symbol the user selects from the dropdown
  selectedTicker: string = '';

  // This holds the initial investment amount the user types in
  principal: number = 0;

  // This holds the number of years the user types in
  years: number = 0;

  // This holds the prediction result returned from Spring Boot
  // It starts as null because we have no result yet
  result: PredictionResponse | null = null;

  // This tracks whether we are currently waiting for the backend to respond
  isLoading: boolean = false;

  // This holds any error message we want to show the user
  errorMessage: string = '';

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

  // This method runs when the user clicks the Calculate button
  onCalculate(): void {

    // Clear any previous error message before starting a new calculation
    this.errorMessage = '';

    // Clear any previous result so old data doesn't show while loading
    this.result = null;

    // Validate that the user actually selected a fund from the dropdown
    if (!this.selectedTicker) {
      // Show an error and stop — do not call the backend
      this.errorMessage = 'Please select a mutual fund.';
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

    // Build the request object to send to Spring Boot
    const request = {
      // Send the selected fund ticker symbol
      ticker: this.selectedTicker,
      // Send the initial investment amount
      principal: this.principal,
      // Send the number of years
      years: this.years
    };

    // Call the prediction service with our request object
    // subscribe() means "when Spring Boot responds, run this function"
    this.predictionService.predict(request).subscribe({

      // next runs when Spring Boot returns a successful prediction
      next: (data: PredictionResponse) => {
        // Store the result so the HTML can display it
        this.result = data;
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
}
