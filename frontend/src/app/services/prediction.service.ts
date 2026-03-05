import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Import request and response
import { PredictionRequest } from '../models/prediction-request.model';
import { PredictionResponse } from '../models/prediction-response.model';

@Injectable({
  providedIn: 'root'
})
export class PredictionService {

  //This is the base URL of our Spring Boot backend
  private baseUrl = 'http://localhost:8080/api';
  constructor(private http: HttpClient) { }

  //Sends POST request to /api/predict with the user's input
  //It returns an Observable that will contain the prediction result
  predict(request: PredictionRequest): Observable<PredictionResponse> {

    //http.post sends a POST request with the request object as the body
    //Spring Boot will receive this as JSON and convert it to PredictionRequest.java
    return this.http.post<PredictionResponse>(`${this.baseUrl}/predict`, request);
  }
}
