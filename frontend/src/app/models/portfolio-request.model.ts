// Represents the request body sent to the portfolio optimizer endpoint
export interface PortfolioRequest {
  tickers: string[];
  riskTolerance: string;
  principal: number;
  years: number;
}
