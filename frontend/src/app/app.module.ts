// This is the root module that registers everything Angular needs
import { NgModule } from '@angular/core';

// BrowserModule gives us basic browser functionality
import { BrowserModule } from '@angular/platform-browser';

// HttpClientModule lets us make HTTP calls to our Spring Boot backend
import { HttpClientModule } from '@angular/common/http';

// FormsModule gives us ngModel so form inputs sync with TypeScript variables
import { FormsModule } from '@angular/forms';

// Our root component
import { AppComponent } from './app.component';

// Our new calculator component that we just created
import { CalculatorComponent } from './components/calculator/calculator.component';

// @NgModule registers all pieces of our Angular app
@NgModule({
  // Every component we create must be listed here
  declarations: [
    AppComponent
  ],

  // External modules we want to use throughout the app
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    CalculatorComponent
  ],

  providers: [],

  // AppComponent is the first thing Angular loads
  bootstrap: [AppComponent]
})
export class AppModule { }
