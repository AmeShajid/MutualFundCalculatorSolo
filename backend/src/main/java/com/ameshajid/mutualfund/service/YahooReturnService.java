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
// This import lets us create HTTP headers to attach to our request
import org.springframework.http.HttpHeaders;
// This import lets us wrap our headers into an HTTP entity (the full request package)
import org.springframework.http.HttpEntity;
// This import lets us specify we are making a GET request
import org.springframework.http.HttpMethod;
// This import lets us hold the full HTTP response including status and body
import org.springframework.http.ResponseEntity;

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
            // Create a new headers object so we can add request headers
            HttpHeaders headers = new HttpHeaders();

            // Add a User-Agent header so Yahoo thinks this is a real browser request
            // Without this Yahoo Finance returns 401 Unauthorized or 403 Forbidden
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36");

            // Wrap our headers inside an HttpEntity — this is the full request package
            // The null means we have no request body (GET requests don't send a body)
            HttpEntity<String> entity = new HttpEntity<>(null, headers);

            // Send the GET request with our headers attached and store the full response
            // String.class tells Spring to give us the response body as a raw String
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Extract just the body text (the raw JSON string) from the response
            String json = response.getBody();

            // If Yahoo returned nothing, we cannot calculate anything so return 0
            if (json == null) {
                return 0.0;
            }

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
