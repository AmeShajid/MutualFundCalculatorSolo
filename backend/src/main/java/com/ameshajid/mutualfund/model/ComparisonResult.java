/**
 ComparisonResult holds the prediction result for a single fund within a comparison.
 Each fund gets its own ComparisonResult containing either a successful prediction or an error message.
 This allows the comparison endpoint to return partial results — if one fund fails, others still show.
 */
package com.ameshajid.mutualfund.model;

public class ComparisonResult {

    //The ticker symbol for this fund
    private String ticker;

    //The display name of this fund
    private String fundName;

    //The prediction result (null if this fund failed)
    private PredictionResponse prediction;

    //The error message (null if this fund succeeded)
    private String error;

    public ComparisonResult() {
    }

    //Constructor for a successful prediction
    public ComparisonResult(String ticker, String fundName, PredictionResponse prediction) {
        this.ticker = ticker;
        this.fundName = fundName;
        this.prediction = prediction;
    }

    //Constructor for a failed prediction
    public ComparisonResult(String ticker, String fundName, String error) {
        this.ticker = ticker;
        this.fundName = fundName;
        this.error = error;
    }

    //Getter for ticker
    public String getTicker() {
        return ticker;
    }

    //Setter for ticker
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    //Getter for fund name
    public String getFundName() {
        return fundName;
    }

    //Setter for fund name
    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    //Getter for prediction (null if fund failed)
    public PredictionResponse getPrediction() {
        return prediction;
    }

    //Setter for prediction
    public void setPrediction(PredictionResponse prediction) {
        this.prediction = prediction;
    }

    //Getter for error message (null if fund succeeded)
    public String getError() {
        return error;
    }

    //Setter for error message
    public void setError(String error) {
        this.error = error;
    }
}
