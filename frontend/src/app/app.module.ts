//Everything angular needs to run
import { NgModule } from '@angular/core';

//Lets us use browser-specific features like the DOM
import { BrowserModule } from '@angular/platform-browser';

//Lets Angular make HTTP calls to our backend
import { HttpClientModule } from '@angular/common/http';

//Starting point of app
import { AppComponent } from './app.component';

//Tell angular this is an app and whats inside
@NgModule({
  //Lists every component
  declarations: [
    AppComponent
  ],
  //lists external stuff we use
  imports: [
    //Gives us browser functionality
    BrowserModule,
    //Lets us make GET and POST calls
    HttpClientModule
  ],

  //Lists services that are available
  providers: [],

  //Tells Angular which component to load first when the app starts
  bootstrap: [AppComponent]
})
//
export class AppModule { }
