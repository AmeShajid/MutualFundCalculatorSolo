//Describes data WE send to backend
export interface PredictionRequest {

  //Ticker symbol user selects
  ticker: string;

  //Inital investment amount
  principal: number;

  //Number of years
  years: number;
}
