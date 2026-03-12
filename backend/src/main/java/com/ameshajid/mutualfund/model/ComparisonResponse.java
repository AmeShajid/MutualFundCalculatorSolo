/**
 ComparisonResponse wraps the results of comparing multiple funds.
 Contains the list of individual fund results plus the input parameters
 echoed back so the frontend can confirm what was calculated.
 */
package com.ameshajid.mutualfund.model;

//Allows us to use List
import java.util.List;

public class ComparisonResponse {

    //List of results, one per fund
    private List<ComparisonResult> results;

    //Echo back the principal so frontend can confirm
    private double principal;

    //Echo back the years so frontend can confirm
    private double years;

    public ComparisonResponse() {
    }

    //Constructor with all fields
    public ComparisonResponse(List<ComparisonResult> results, double principal, double years) {
        this.results = results;
        this.principal = principal;
        this.years = years;
    }

    //Getter for results list
    public List<ComparisonResult> getResults() {
        return results;
    }

    //Setter for results list
    public void setResults(List<ComparisonResult> results) {
        this.results = results;
    }

    //Getter for principal
    public double getPrincipal() {
        return principal;
    }

    //Setter for principal
    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    //Getter for years
    public double getYears() {
        return years;
    }

    //Setter for years
    public void setYears(double years) {
        this.years = years;
    }
}
