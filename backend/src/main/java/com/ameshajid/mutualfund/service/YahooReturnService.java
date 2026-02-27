/**
 YahooReturnService calls the Yahoo Finance API to get 1 year of price data for a stock or fund.
 Requests monthly closing prices for the last year
 Finds the first valid closing price
 Finds the last valid closing price
 Calculates the percentage return
 Returns that return value
 If anything fails it safely returns 0.0.
 */
package com.ameshajid.mutualfund.service;
// This import allows us to work with JSON nodes
import com.fasterxml.jackson.databind.JsonNode;
// This import allows us to convert raw JSON text into a tree structure
import com.fasterxml.jackson.databind.ObjectMapper;
// This tells Spring this class is a service component
import org.springframework.stereotype.Service;
// This allows us to make HTTP requests to external APIs
import org.springframework.web.client.RestTemplate;

//This class as a Spring service
@Service
public class YahooReturnService {
    //URL for yfinance
    private static final String BASE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/";

    //Used to send HTTP
    private final RestTemplate restTemplate = new RestTemplate();

    //To parse JSON strings into JSON objects
    private final ObjectMapper objectMapper = new ObjectMapper();

    //Calculates last 1-year return
    public double getLastYearReturn(String symbol) {

        // If the symbol is missing return 0
        if (symbol == null || symbol.trim().isEmpty()) {
            return 0.0;
        }

        //Make yahoo URL for 1 year with monthly intervals
        String url = BASE_URL + symbol.trim() + "?range=1y&interval=1mo";

        try {
            //Send http req and get raw json as a strong
            String json = restTemplate.getForObject(url, String.class);

            //Convert json string to json node tree
            JsonNode root = objectMapper.readTree(json);

            //get the close price
            JsonNode closeArray =
                    root.path("chart").path("result").get(0).path("indicators").path("quote").get(0).path("close");

            //stores the first and last closing price
            Double firstClose = null;
            Double lastClose = null;

            //iterate through array
            for (int i = 0; i < closeArray.size(); i++) {

                //if curr closing price is not null
                if (!closeArray.get(i).isNull()) {

                    ///convert json value to double and store
                    firstClose = closeArray.get(i).asDouble();

                    //stop when we find first vaild closing price
                    break;
                }
            }

            //iterate through array
            for (int i = closeArray.size() - 1; i >= 0; i--) {

                //if curr closing price is not null
                if (!closeArray.get(i).isNull()) {

                    //convert json value to double and store
                    lastClose = closeArray.get(i).asDouble();

                    //stop when we find last vaild closing price
                    break;
                }
            }

            //If value is invalid
            if (firstClose == null || lastClose == null || firstClose == 0.0) {
                return 0.0;
            }

            //get difference
            double priceDifference = lastClose - firstClose;

            //get return rate
            double returnRate = priceDifference / firstClose;

            // return the calculated return rate
            return returnRate;

            //if any error happens
        } catch (Exception e) {
            return 0.0;
        }
    }
}
