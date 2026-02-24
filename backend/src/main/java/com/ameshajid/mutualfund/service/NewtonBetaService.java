package com.ameshajid.mutualfund.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NewtonBetaService {

    // Newton Analytics API
    private static final String BASE_URL = "https://api.newtonanalytics.com/stock-beta/";

    private final RestTemplate restTemplate = new RestTemplate();

    public String getBeta(String ticker, String index, String interval, int observations) {

        //Checking if the ticker exist
        if (ticker == null || ticker.trim().isEmpty()) {
            return "{\"status\":400,\"statusMessage\":\"ticker is required\"}";
        }

        //Default ticker
        if (index == null || index.trim().isEmpty()) {
            index = "^GSPC";
        }

        //Default time
        if (interval == null || interval.trim().isEmpty()) {
            interval = "1mo";
        }

        //Default observations
        if (observations <= 0) {
            observations = 10;
        }

        //^ we need encode this for the URL
        String tickerEncoded = URLEncoder.encode(ticker.trim(), StandardCharsets.UTF_8);
        String indexEncoded = URLEncoder.encode(index.trim(), StandardCharsets.UTF_8);
        String intervalEncoded = URLEncoder.encode(interval.trim(), StandardCharsets.UTF_8);

        //creating the URL
        String url = BASE_URL + "?ticker=" + tickerEncoded + "&index=" + indexEncoded + "&interval=" + intervalEncoded + "&observations=" + observations;

        //return JSON string
        return restTemplate.getForObject(url, String.class);
    }
}