/**
 This class represents the data the frontend sends to backend when the user clicks “Calculate”.
 ticker - the fund symbol
 principal - initial investment amount
 years - number of years investing
 Spring automatically converts incoming JSON into this object.
 This class only holds data no logic.
 */
package com.ameshajid.mutualfund.model;

//When user sends data from frontend
public class PredictionRequest {

    //storing ticker symbol
    private String ticker;

    //storing initial investment
    private double principal;

    //storing number of years
    private double years;

    public PredictionRequest() {

    }

    // This getter returns the ticker symbol
    public String getTicker() {
        return ticker;
    }

    // This setter sets the ticker symbol
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    // This getter returns the principal amount
    public double getPrincipal() {
        return principal;
    }

    // This setter sets the principal amount
    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    // This getter returns the number of years
    public double getYears() {
        return years;
    }

    // This setter sets the number of years
    public void setYears(double years) {
        this.years = years;
    }
}