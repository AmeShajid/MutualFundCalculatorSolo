import { PortfolioRecommendation } from './portfolio-recommendation.model';

export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  portfolioData?: PortfolioRecommendation;
}
