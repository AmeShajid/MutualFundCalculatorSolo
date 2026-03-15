export interface PortfolioChatRequest {
  conversationHistory: { role: string; content: string }[];
  tickers: string[];
  riskTolerance: string;
  principal: number;
  years: number;
}
