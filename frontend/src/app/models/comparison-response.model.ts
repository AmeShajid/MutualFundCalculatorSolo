//Wraps all fund comparison results plus the input parameters
import { ComparisonResult } from './comparison-result.model';

export interface ComparisonResponse {

  //List of results, one per fund
  results: ComparisonResult[];

  //The principal amount that was used
  principal: number;

  //The years that were used
  years: number;
}
