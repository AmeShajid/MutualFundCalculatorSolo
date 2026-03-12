//Holds the result for a single fund within a comparison
import { PredictionResponse } from './prediction-response.model';

export interface ComparisonResult {

  //The ticker symbol for this fund
  ticker: string;

  //The display name of this fund
  fundName: string;

  //The prediction result (null if this fund failed)
  prediction: PredictionResponse | null;

  //The error message (null if this fund succeeded)
  error: string | null;
}
