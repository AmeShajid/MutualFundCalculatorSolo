//This is for a fund OBJECT
export interface Fund {
  //ticker symbol
  symbol: string;

  //The full ticker name
  name: string;

  //Fund type: MUTUAL_FUND or ETF
  type: string;
}
