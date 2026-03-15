package com.ameshajid.mutualfund.model;

public class Fund {
    private String symbol;
    private String name;
    private String type;

    public Fund() {
    }

    public Fund(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
        this.type = "MUTUAL_FUND";
    }

    public Fund(String symbol, String name, String type) {
        this.symbol = symbol;
        this.name = name;
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
