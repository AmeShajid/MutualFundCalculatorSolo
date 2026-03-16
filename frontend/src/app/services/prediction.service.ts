import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Import response models
import { PredictionResponse } from '../models/prediction-response.model';
import { ComparisonResponse } from '../models/comparison-response.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PredictionService {

  //This is the base URL of our Spring Boot backend
  private baseUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }

  //Sends GET request to /api/predict with query parameters for a single fund
  //It returns an Observable that will contain the prediction result
  predict(ticker: string, principal: number, years: number): Observable<PredictionResponse> {

    //Build query parameters for the GET request
    const params = new HttpParams()
      .set('ticker', ticker)
      .set('principal', principal.toString())
      .set('years', years.toString());

    //http.get sends a GET request with query params
    //Spring Boot will extract these from the URL as @RequestParam
    return this.http.get<PredictionResponse>(`${this.baseUrl}/predict`, { params });
  }

  //Sends GET request to /api/predict/compare with multiple tickers
  //Runs all fund predictions in parallel on the backend
  compare(tickers: string[], principal: number, years: number): Observable<ComparisonResponse> {

    //Build query parameters — append each ticker separately so Spring maps to List<String>
    let params = new HttpParams()
      .set('principal', principal.toString())
      .set('years', years.toString());

    //Add each ticker as a separate query param (e.g., ?tickers=VSMPX&tickers=FXAIX)
    for (const ticker of tickers) {
      params = params.append('tickers', ticker);
    }

    //http.get sends a GET request with query params
    return this.http.get<ComparisonResponse>(`${this.baseUrl}/predict/compare`, { params });
  }
}
