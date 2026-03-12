/**
 This class represents the data backend sends back to the frontend after calculating the prediction.
 futureValue - the calculated investment value
 beta - beta used in CAPM
 expectedReturn - expected market return
 riskFreeRate - risk-free rate used in calculation
 This is the final result object that gets converted into JSON and returned to Angular.
 */
package com.ameshajid.mutualfund.model;
//data from backend sent to frontend
public class PredictionResponse {
    // Storing future value of the investment
    private double futureValue;
    // Storing beta value used in formula
    private double beta;
    //Storing expected return
    private double expectedReturn;
    //Storing risk-free rate
    private double riskFreeRate;
    //Storing optional warning message when CAPM may not be reliable
    private String warning;

    public PredictionResponse() {
    }

    //Constructor allows to object
    public PredictionResponse(double futureValue, double beta, double expectedReturn, double riskFreeRate) {
        this.futureValue = futureValue;
        this.beta = beta;
        this.expectedReturn = expectedReturn;
        this.riskFreeRate = riskFreeRate;
    }

    //getter returns calculated future value
    public double getFutureValue() {
        return futureValue;
    }

    //setter updates future value
    public void setFutureValue(double futureValue) {
        this.futureValue = futureValue;
    }

    //getter returns beta value
    public double getBeta() {
        return beta;
    }

    //setter updates beta value
    public void setBeta(double beta) {
        this.beta = beta;
    }

    //getter returns expected return
    public double getExpectedReturn() {
        return expectedReturn;
    }

    //setter updates expected return
    public void setExpectedReturn(double expectedReturn) {
        this.expectedReturn = expectedReturn;
    }

    //getter returns risk-free rate
    public double getRiskFreeRate() {
        return riskFreeRate;
    }

    //setter updates risk-free rate
    public void setRiskFreeRate(double riskFreeRate) {
        this.riskFreeRate = riskFreeRate;
    }

    //getter returns warning message (null if no warning)
    public String getWarning() {
        return warning;
    }

    //setter updates warning message
    public void setWarning(String warning) {
        this.warning = warning;
    }
}