/**
 This class represents the JSON response you receive from the Newton Analytics Beta API.
 When call Newton’s API to get a stock’s beta, Spring converts the JSON response into this Java object.
 This class does not contain logic it just holds API response data (status, beta value, etc.).
 */
package com.ameshajid.mutualfund.model;

//Json structure returned by Newton API
public class NewtonBetaApiResponse {

    //Api status
    private String status;

    //Api status message
    private String statusMessage;

    //Actual beta value
    private Double data;

    //Disclaimer Text
    private String disclaimer;

    //so this is called a no argument constructor for JSON deserialization
    //Spring needs this to create the object when parsing JSON
    public NewtonBetaApiResponse() {

    }

    // This getter returns the API status
    public String getStatus() {
        return status;
    }

    // This setter sets the API status value
    public void setStatus(String status) {
        this.status = status;
    }

    // This getter returns the API status message
    public String getStatusMessage() {
        return statusMessage;
    }

    // This setter sets the API status message
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    // This getter returns the beta value
    public Double getData() {
        return data;
    }

    // This setter sets the beta value
    public void setData(Double data) {
        this.data = data;
    }

    // This getter returns the API disclaimer
    public String getDisclaimer() {
        return disclaimer;
    }

    // This setter sets the disclaimer text
    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}