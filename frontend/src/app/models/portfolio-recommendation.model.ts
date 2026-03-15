// Represents the complete AI portfolio optimization response
import { FundAllocation } from './fund-allocation.model';

export interface PortfolioRecommendation {
  allocations: FundAllocation[];
  reasoning: string;
  riskAssessment: string;
  riskTolerance: string;
  principal: number;
  years: number;
}
