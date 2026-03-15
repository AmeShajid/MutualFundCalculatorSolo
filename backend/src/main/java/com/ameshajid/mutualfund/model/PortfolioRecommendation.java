/**
 PortfolioRecommendation holds the complete AI response for portfolio optimization.
 Contains the allocation breakdown, AI reasoning, and risk assessment.
 */
package com.ameshajid.mutualfund.model;

import java.util.List;

public class PortfolioRecommendation {

    //List of fund allocations recommended by the AI
    private List<FundAllocation> allocations;

    //AI's reasoning for the allocation decisions
    private String reasoning;

    //AI's assessment of the portfolio's overall risk profile
    private String riskAssessment;

    //The risk tolerance level that was requested
    private String riskTolerance;

    //The principal amount that was invested
    private double principal;

    //The investment time horizon in years
    private double years;

    //Legal disclaimer that this is not financial advice
    private String disclaimer;

    public List<FundAllocation> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<FundAllocation> allocations) {
        this.allocations = allocations;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public String getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
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

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
