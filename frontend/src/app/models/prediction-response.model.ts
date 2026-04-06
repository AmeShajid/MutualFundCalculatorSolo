//Response from the backend after CAPM calculation
export interface PredictionResponse {

  //Calculated future value of the investment
  futureValue: number;

  //Beta value used in the CAPM formula
  beta: number;

  //Expected annual return rate
  expectedReturn: number;

  //Risk-free rate used in the calculation
  riskFreeRate: number;

  //Optional warning when CAPM may not be reliable (e.g., money market funds)
  warning?: string;
}
