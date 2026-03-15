import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { PortfolioRequest } from '../models/portfolio-request.model';
import { PortfolioRecommendation } from '../models/portfolio-recommendation.model';

@Injectable({
  providedIn: 'root'
})
export class PortfolioService {

  //Base URL of the Spring Boot backend
  private baseUrl = 'http://localhost:8080/api/portfolio';

  constructor(private http: HttpClient) {}

  //Sends POST request to the portfolio optimizer endpoint
  //Returns an Observable with the AI's portfolio recommendation
  optimize(request: PortfolioRequest): Observable<PortfolioRecommendation> {
    return this.http.post<PortfolioRecommendation>(`${this.baseUrl}/optimize`, request);
  }
}
