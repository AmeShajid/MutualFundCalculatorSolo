package com.ameshajid.mutualfund.model;

public class PredictionResponse {
    private double futureValue;
    private double beta;
    private double expectedReturn;
    private double riskFreeRate;

    public PredictionResponse() {}

    public PredictionResponse(double futureValue, double beta, double expectedReturn, double riskFreeRate) {
        this.futureValue = futureValue;
        this.beta = beta;
        this.expectedReturn = expectedReturn;
        this.riskFreeRate = riskFreeRate;
    }

    public double getFutureValue() { return futureValue; }
    public void setFutureValue(double futureValue) { this.futureValue = futureValue; }

    public double getBeta() { return beta; }
    public void setBeta(double beta) { this.beta = beta; }

    public double getExpectedReturn() { return expectedReturn; }
    public void setExpectedReturn(double expectedReturn) { this.expectedReturn = expectedReturn; }

    public double getRiskFreeRate() { return riskFreeRate; }
    public void setRiskFreeRate(double riskFreeRate) { this.riskFreeRate = riskFreeRate; }
}