/**
 NewtonBetaService calls the Newton Analytics API to get the beta value for a stock or mutual fund.
 Validates inputs
 Sets default values if needed
 Builds the API URL
 Calls the external API using RestTemplate
 Extracts and returns the beta value
 This is where your backend connects to an external financial API.
 */
package com.ameshajid.mutualfund.service;
// This import allows to encode URL parameters safely
import java.net.URLEncoder;
// This import provides standard character encoding
import java.nio.charset.StandardCharsets;
// This import gives us a logger to record events and errors
import org.slf4j.Logger;
// This import creates a logger instance for this class
import org.slf4j.LoggerFactory;
// Tells Spring this class is a service component
import org.springframework.stereotype.Service;
// This allows us to make HTTP requests to external APIs
import org.springframework.web.client.RestTemplate;
// This import allows us to use the NewtonBetaApiResponse model
import com.ameshajid.mutualfund.model.NewtonBetaApiResponse;

//This class as a Spring service
@Service
public class NewtonBetaService {
    //Logger for recording events and errors in this service
    private static final Logger log = LoggerFactory.getLogger(NewtonBetaService.class);
    //URL for newton analytics
    private static final String BASE_URL = "https://api.newtonanalytics.com/stock-beta/";
    //Used to make HTTP req — injected from RestTemplateConfig bean
    private final RestTemplate restTemplate;

    //Constructor allows Spring to inject the shared RestTemplate
    public NewtonBetaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    //gets beta value for any stock
    public double getBeta(String ticker, String index, String interval, int observations) {

        //if ticker is missing, given an error
        if (ticker == null || ticker.trim().isEmpty()) {
            log.error("Ticker is null or empty");
            throw new IllegalArgumentException("ticker is required");
        }

        log.info("Fetching beta from Newton Analytics for {}", ticker);

        //If index is missing, use SNP
        if (index == null || index.trim().isEmpty()) {
            index = "^GSPC";
        }

        //If interval is missing, use 1mo
        if (interval == null || interval.trim().isEmpty()) {
            interval = "1mo";
        }

        //If observations is missing, use 10
        if (observations <= 0) {
            observations = 10;
        }

        // Encode ticker for URL usage
        String tickerEncoded = URLEncoder.encode(ticker.trim(), StandardCharsets.UTF_8);

        // Encode index for URL usage
        String indexEncoded = URLEncoder.encode(index.trim(), StandardCharsets.UTF_8);

        // Encode interval for URL usage
        String intervalEncoded = URLEncoder.encode(interval.trim(), StandardCharsets.UTF_8);

        //Creating full URL
        String url = BASE_URL + "?ticker=" + tickerEncoded + "&index=" + indexEncoded + "&interval=" + intervalEncoded + "&observations=" + observations;

        //Make HTTP req + map json into NewtonBeta
        NewtonBetaApiResponse response;
        try {
            response = restTemplate.getForObject(url, NewtonBetaApiResponse.class);
        } catch (Exception e) {
            //Wrap any API or parsing errors in a user-friendly message
            log.error("Newton API call failed for {}", ticker, e);
            throw new RuntimeException("Beta data is not available for " + ticker);
        }

        //If response or beta data is missing throw error
        if (response == null || response.getData() == null) {
            log.error("Newton API returned no beta data for {}", ticker);
            throw new RuntimeException("Beta data is not available for " + ticker);
        }

        log.info("Newton Analytics beta for {}: {}", ticker, response.getData());

        //return beta value
        return response.getData();
    }
}