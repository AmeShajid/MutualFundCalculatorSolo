package com.ameshajid.mutualfund.model;

public class PredictionRequest {
    private String ticker;
    private double principal;
    private double years;

    public PredictionRequest() {}

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public double getPrincipal() { return principal; }
    public void setPrincipal(double principal) { this.principal = principal; }

    public double getYears() { return years; }
    public void setYears(double years) { this.years = years; }
}
