//Makes this class injectable as a singleton service
import { Injectable } from '@angular/core';

//HttpClient for making HTTP requests to the backend
import { HttpClient } from '@angular/common/http';

//Observable for handling async data streams
import { Observable } from 'rxjs';

//Fund interface so we can type the response correctly
import { Fund } from '../models/fund.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FundService {

  //Base URL for all API calls
  private baseUrl = environment.apiUrl;

  // Angular injects HttpClient here when the service is created
  constructor(private http: HttpClient) { }

  //Sends GET /api/funds and returns the list of available funds
  getFunds(): Observable<Fund[]> {
    return this.http.get<Fund[]>(`${this.baseUrl}/funds`);
  }
}
