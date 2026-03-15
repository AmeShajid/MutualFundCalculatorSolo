import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { PortfolioRequest } from '../models/portfolio-request.model';
import { PortfolioRecommendation } from '../models/portfolio-recommendation.model';
import { PortfolioChatRequest } from '../models/portfolio-chat-request.model';
import { PortfolioChatResponse } from '../models/portfolio-chat-response.model';

@Injectable({
  providedIn: 'root'
})
export class PortfolioService {

  private baseUrl = 'http://localhost:8080/api/portfolio';

  constructor(private http: HttpClient) {}

  optimize(request: PortfolioRequest): Observable<PortfolioRecommendation> {
    return this.http.post<PortfolioRecommendation>(`${this.baseUrl}/optimize`, request);
  }

  chat(request: PortfolioChatRequest): Observable<PortfolioChatResponse> {
    return this.http.post<PortfolioChatResponse>(`${this.baseUrl}/chat`, request);
  }
}
