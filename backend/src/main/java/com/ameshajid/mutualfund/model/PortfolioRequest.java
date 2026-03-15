/**
 PortfolioRequest holds the input data for the AI portfolio optimizer.
 The frontend sends this as JSON in a POST request body.
 */
package com.ameshajid.mutualfund.model;

import java.util.List;

public class PortfolioRequest {

    //List of fund ticker symbols the user selected
    private List<String> tickers;

    //User's risk tolerance level: "conservative", "moderate", or "aggressive"
    private String riskTolerance;

    //The initial investment amount in dollars
    private double principal;

    //The investment time horizon in years
    private double years;

    public List<String> getTickers() {
        return tickers;
    }

    public void setTickers(List<String> tickers) {
        this.tickers = tickers;
    }

    public String getRiskTolerance() {
        return riskTolerance;
    }

    public void setRiskTolerance(String riskTolerance) {
        this.riskTolerance = riskTolerance;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    public double getYears() {
        return years;
    }

    public void setYears(double years) {
        this.years = years;
    }
}
