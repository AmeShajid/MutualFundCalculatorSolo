package com.ameshajid.mutualfund.model;

import java.util.List;
import java.util.Map;

public class PortfolioChatRequest {

    private List<Map<String, String>> conversationHistory;
    private List<String> tickers;
    private String riskTolerance;
    private double principal;
    private double years;

    public List<Map<String, String>> getConversationHistory() {
        return conversationHistory;
    }

    public void setConversationHistory(List<Map<String, String>> conversationHistory) {
        this.conversationHistory = conversationHistory;
    }

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
