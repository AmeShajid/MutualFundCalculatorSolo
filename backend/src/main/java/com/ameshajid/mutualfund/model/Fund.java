/**
This is just making all of our setters getters objects for funds
 */
package com.ameshajid.mutualfund.model;

public class Fund {
    //this is the ticker string
    private String symbol;
    //this is the full name
    private String name;
    //For springboot idk why we need an empty constructor
    public Fund() {
    }

    //Creating our objects
    public Fund(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

    // Setter for symbol
    public String getSymbol() {

        return symbol;
    }
    // Setter for symbol
    public String getName() {

        return name;
    }

    // Getter for name
    public void setSymbol(String symbol) {

        this.symbol = symbol;
    }
    // Setter for name
    public void setName(String name) {

        this.name = name;
    }
}
