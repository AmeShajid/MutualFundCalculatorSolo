//Data we recive POST calculation
export interface PredictionResponse {

  //future value of calc
  futureValue: number;

  //beta value that was sued
  beta: number;

  //expected return rate
  expectedReturn: number;

  //risk free rate
  riskFreeRate: number;

  //optional warning message when CAPM may not be reliable
  warning?: string;
}
