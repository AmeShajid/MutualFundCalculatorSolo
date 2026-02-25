package com.ameshajid.mutualfund.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class YahooReturnService {
    private static final String BASE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public double getLastYearReturn(String symbol) {

        if (symbol == null || symbol.trim().isEmpty()) {
            return 0.0;
        }

        String url = BASE_URL + symbol.trim() + "?range=1y&interval=1mo";

        try {
            String json = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(json);

            JsonNode closeArray =
                    root.path("chart")
                            .path("result")
                            .get(0)
                            .path("indicators")
                            .path("quote")
                            .get(0)
                            .path("close");

            Double firstClose = null;
            Double lastClose = null;

            for (int i = 0; i < closeArray.size(); i++) {
                if (!closeArray.get(i).isNull()) {
                    firstClose = closeArray.get(i).asDouble();
                    break;
                }
            }

            for (int i = closeArray.size() - 1; i >= 0; i--) {
                if (!closeArray.get(i).isNull()) {
                    lastClose = closeArray.get(i).asDouble();
                    break;
                }
            }

            if (firstClose == null || lastClose == null || firstClose == 0.0) {
                return 0.0;
            }

            return (lastClose - firstClose) / firstClose;

        } catch (Exception e) {
            return 0.0;
        }
    }
}
