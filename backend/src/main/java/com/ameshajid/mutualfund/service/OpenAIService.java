/**
 OpenAIService calls the OpenAI GPT-4 API to get portfolio allocation recommendations.
 It builds a structured prompt with actual CAPM prediction data and parses the AI's JSON response.
 */
package com.ameshajid.mutualfund.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ameshajid.mutualfund.model.ComparisonResult;
import com.ameshajid.mutualfund.model.FundAllocation;
import com.ameshajid.mutualfund.model.PortfolioRecommendation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private static final Logger log = LoggerFactory.getLogger(OpenAIService.class);

    //RestTemplate with longer timeout for GPT-4 API calls
    private final RestTemplate restTemplate;

    //OpenAI API key loaded from application.properties
    private final String apiKey;

    //Jackson ObjectMapper for parsing JSON responses
    private final ObjectMapper objectMapper = new ObjectMapper();

    //OpenAI API endpoint
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public OpenAIService(
            @Qualifier("openAiRestTemplate") RestTemplate restTemplate,
            @Value("${openai.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    /**
     Gets a portfolio allocation recommendation from GPT-4 based on prediction data.
     Takes the actual CAPM predictions for each fund and returns an optimized allocation.
     */
    public PortfolioRecommendation getRecommendation(
            List<ComparisonResult> predictions,
            String riskTolerance,
            double principal,
            double years) {

        //Build the prompt with actual prediction data
        String prompt = buildPrompt(predictions, riskTolerance, principal, years);

        log.info("Sending portfolio optimization request to OpenAI for {} funds", predictions.size());

        try {
            //Set up HTTP headers with API key and content type
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            //Build the request body for the OpenAI API
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4",
                    "temperature", 0.3,
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "You are a financial portfolio optimizer. You analyze mutual fund data and suggest optimal allocations. Always respond with valid JSON only, no markdown formatting."),
                            Map.of("role", "user", "content", prompt)
                    )
            );

            //Send the request to OpenAI
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            String responseJson = restTemplate.postForObject(OPENAI_API_URL, request, String.class);

            //Parse the OpenAI response to get the content
            JsonNode root = objectMapper.readTree(responseJson);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            log.info("Received OpenAI response, parsing allocation data");

            //Strip markdown code fences if the AI wrapped the JSON in them
            content = stripCodeFences(content);

            //Parse the AI's JSON response into our recommendation model
            return parseRecommendation(content, predictions, riskTolerance, principal, years);

        } catch (Exception e) {
            log.error("OpenAI API call failed", e);
            throw new RuntimeException("AI portfolio optimization failed. Please check your API key and try again.");
        }
    }

    /**
     Builds a detailed prompt with all the prediction data for the AI to analyze.
     */
    private String buildPrompt(List<ComparisonResult> predictions, String riskTolerance,
                               double principal, double years) {

        StringBuilder sb = new StringBuilder();
        sb.append("I have $").append(String.format("%.2f", principal));
        sb.append(" to invest over ").append(years).append(" years.\n");
        sb.append("My risk tolerance is: ").append(riskTolerance).append(".\n\n");
        sb.append("Here are the CAPM-based predictions for my selected mutual funds:\n\n");

        for (ComparisonResult result : predictions) {
            sb.append("Fund: ").append(result.getFundName());
            sb.append(" (").append(result.getTicker()).append(")\n");

            if (result.getPrediction() != null) {
                sb.append("  Beta: ").append(String.format("%.4f", result.getPrediction().getBeta())).append("\n");
                sb.append("  Expected Return: ").append(String.format("%.4f", result.getPrediction().getExpectedReturn())).append("\n");
                sb.append("  Projected Future Value (of $").append(String.format("%.2f", principal)).append("): $");
                sb.append(String.format("%.2f", result.getPrediction().getFutureValue())).append("\n");

                if (result.getPrediction().getWarning() != null) {
                    sb.append("  Warning: ").append(result.getPrediction().getWarning()).append("\n");
                }
            } else {
                sb.append("  Error: ").append(result.getError()).append("\n");
            }
            sb.append("\n");
        }

        sb.append("Based on this data and my risk tolerance, suggest how to split my $");
        sb.append(String.format("%.2f", principal));
        sb.append(" across these funds.\n\n");
        sb.append("Respond in this exact JSON format (no other text):\n");
        sb.append("{\n");
        sb.append("  \"allocations\": [\n");
        sb.append("    {\n");
        sb.append("      \"ticker\": \"SYMBOL\",\n");
        sb.append("      \"allocationPercentage\": 50.0\n");
        sb.append("    }\n");
        sb.append("  ],\n");
        sb.append("  \"reasoning\": \"Explanation of why this allocation was chosen\",\n");
        sb.append("  \"riskAssessment\": \"Assessment of the portfolio's overall risk\"\n");
        sb.append("}\n\n");
        sb.append("Rules:\n");
        sb.append("- Allocation percentages must sum to exactly 100\n");
        sb.append("- Only include funds that had successful predictions (no errors)\n");
        sb.append("- For conservative: favor lower beta funds\n");
        sb.append("- For aggressive: favor higher return funds\n");
        sb.append("- For moderate: balance between risk and return\n");

        return sb.toString();
    }

    /**
     Strips markdown code fences (```json ... ```) from the AI response if present.
     */
    private String stripCodeFences(String content) {
        content = content.trim();
        if (content.startsWith("```json")) {
            content = content.substring(7);
        } else if (content.startsWith("```")) {
            content = content.substring(3);
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3);
        }
        return content.trim();
    }

    /**
     Parses the AI's JSON response and enriches it with actual prediction data.
     */
    private PortfolioRecommendation parseRecommendation(
            String content,
            List<ComparisonResult> predictions,
            String riskTolerance,
            double principal,
            double years) throws Exception {

        JsonNode aiResponse = objectMapper.readTree(content);

        //Parse allocations from AI response
        List<FundAllocation> allocations = new ArrayList<>();
        JsonNode allocationsNode = aiResponse.path("allocations");
        double totalPercentage = 0;

        for (JsonNode node : allocationsNode) {
            String ticker = node.path("ticker").asText();
            double percentage = node.path("allocationPercentage").asDouble();
            totalPercentage += percentage;

            FundAllocation allocation = new FundAllocation();
            allocation.setTicker(ticker);
            allocation.setAllocationPercentage(percentage);

            //Find the matching prediction to enrich with actual data
            for (ComparisonResult result : predictions) {
                if (result.getTicker().equalsIgnoreCase(ticker) && result.getPrediction() != null) {
                    allocation.setFundName(result.getFundName());
                    allocation.setBeta(result.getPrediction().getBeta());
                    allocation.setExpectedReturn(result.getPrediction().getExpectedReturn());

                    //Calculate allocated amount and projected value based on percentage
                    double allocatedAmount = principal * percentage / 100.0;
                    allocation.setAllocatedAmount(allocatedAmount);

                    //Scale the projected value proportionally
                    double fullProjection = result.getPrediction().getFutureValue();
                    allocation.setProjectedValue(fullProjection * percentage / 100.0);
                    break;
                }
            }

            allocations.add(allocation);
        }

        //Normalize percentages if they don't sum to 100
        if (Math.abs(totalPercentage - 100.0) > 0.01 && totalPercentage > 0) {
            log.warn("AI allocation percentages sum to {}, normalizing to 100", totalPercentage);
            for (FundAllocation allocation : allocations) {
                double normalized = allocation.getAllocationPercentage() * 100.0 / totalPercentage;
                allocation.setAllocationPercentage(normalized);
                allocation.setAllocatedAmount(principal * normalized / 100.0);
            }
        }

        //Build the final recommendation
        PortfolioRecommendation recommendation = new PortfolioRecommendation();
        recommendation.setAllocations(allocations);
        recommendation.setReasoning(aiResponse.path("reasoning").asText());
        recommendation.setRiskAssessment(aiResponse.path("riskAssessment").asText());
        recommendation.setRiskTolerance(riskTolerance);
        recommendation.setPrincipal(principal);
        recommendation.setYears(years);

        return recommendation;
    }
}
