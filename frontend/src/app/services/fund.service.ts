//makes the class injectible
import { Injectable } from '@angular/core';

//so we can make http req
import { HttpClient } from '@angular/common/http';

//for data that comes in teh future
import { Observable } from 'rxjs';

//so we can type response correcrly
import { Fund } from '../models/fund.model';

//so it can be used in app
@Injectable({
  providedIn: 'root'
})
export class FundService {

  //base url all api calls start with this
  private baseUrl = 'http://localhost:8080/api';

  // Angular injects HttpClient here when service is created
  constructor(private http: HttpClient) { }

  //Method called: GET /api/funds and returns list of Fund objects
  // Observable<Fund[]> means "this will eventually give us an array of Fund objects"
  getFunds(): Observable<Fund[]> {

    // http.get sends GET request to our backend funds
    // Fund[] tells TypeScript what shape the response data will be
    return this.http.get<Fund[]>(`${this.baseUrl}/funds`);
  }
}
