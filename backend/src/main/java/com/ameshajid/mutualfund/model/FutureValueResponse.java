package com.ameshajid.mutualfund.model;

public class FutureValueResponse {

    private String symbol;
    private double principal;
    private int years;

    private double riskFreeRate;
    private double expectedReturnRate;
    private double beta;

    private double capmRate;
    private double futureValue;

    public FutureValueResponse() {
    }

    public FutureValueResponse(
            String symbol,
            double principal,
            int years,
            double riskFreeRate,
            double expectedReturnRate,
            double beta,
            double capmRate,
            double futureValue
    ) {
        this.symbol = symbol;
        this.principal = principal;
        this.years = years;
        this.riskFreeRate = riskFreeRate;
        this.expectedReturnRate = expectedReturnRate;
        this.beta = beta;
        this.capmRate = capmRate;
        this.futureValue = futureValue;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public double getRiskFreeRate() {
        return riskFreeRate;
    }

    public void setRiskFreeRate(double riskFreeRate) {
        this.riskFreeRate = riskFreeRate;
    }

    public double getExpectedReturnRate() {
        return expectedReturnRate;
    }

    public void setExpectedReturnRate(double expectedReturnRate) {
        this.expectedReturnRate = expectedReturnRate;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getCapmRate() {
        return capmRate;
    }

    public void setCapmRate(double capmRate) {
        this.capmRate = capmRate;
    }

    public double getFutureValue() {
        return futureValue;
    }

    public void setFutureValue(double futureValue) {
        this.futureValue = futureValue;
    }
}
