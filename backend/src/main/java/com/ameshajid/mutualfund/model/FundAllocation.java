/**
 FundAllocation represents the AI's recommended allocation for a single fund.
 Each fund in the portfolio gets one of these with its percentage and projected value.
 */
package com.ameshajid.mutualfund.model;

public class FundAllocation {

    //The fund's ticker symbol
    private String ticker;

    //The fund's display name
    private String fundName;

    //What percentage of the total investment to put in this fund
    private double allocationPercentage;

    //The dollar amount allocated to this fund (principal * percentage / 100)
    private double allocatedAmount;

    //The projected future value of this allocation based on CAPM
    private double projectedValue;

    //The fund's beta value from CAPM calculation
    private double beta;

    //The fund's expected return from CAPM calculation
    private double expectedReturn;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public double getAllocationPercentage() {
        return allocationPercentage;
    }

    public void setAllocationPercentage(double allocationPercentage) {
        this.allocationPercentage = allocationPercentage;
    }

    public double getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public double getProjectedValue() {
        return projectedValue;
    }

    public void setProjectedValue(double projectedValue) {
        this.projectedValue = projectedValue;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getExpectedReturn() {
        return expectedReturn;
    }

    public void setExpectedReturn(double expectedReturn) {
        this.expectedReturn = expectedReturn;
    }
}
