import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Import response model
import { PredictionResponse } from '../models/prediction-response.model';

@Injectable({
  providedIn: 'root'
})
export class PredictionService {

  //This is the base URL of our Spring Boot backend
  private baseUrl = 'http://localhost:8080/api';
  constructor(private http: HttpClient) { }

  //Sends GET request to /api/predict with query parameters
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
}
