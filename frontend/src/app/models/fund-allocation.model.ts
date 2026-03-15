// Represents the AI's recommended allocation for a single fund
export interface FundAllocation {
  ticker: string;
  fundName: string;
  allocationPercentage: number;
  allocatedAmount: number;
  projectedValue: number;
  beta: number;
  expectedReturn: number;
}
