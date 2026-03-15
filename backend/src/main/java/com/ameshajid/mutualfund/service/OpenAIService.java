/**
 OpenAIService calls the Google Gemini API to get portfolio allocation recommendations.
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

    //RestTemplate with longer timeout for Gemini API calls
    private final RestTemplate restTemplate;

    //Gemini API key loaded from application.properties
    private final String apiKey;

    //Jackson ObjectMapper for parsing JSON responses
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenAIService(
            @Qualifier("openAiRestTemplate") RestTemplate restTemplate,
            @Value("${gemini.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    /**
     Gets a portfolio allocation recommendation from Gemini based on prediction data.
     Takes the actual CAPM predictions for each fund and returns an optimized allocation.
     */
    public PortfolioRecommendation getRecommendation(
            List<ComparisonResult> predictions,
            String riskTolerance,
            double principal,
            double years) {

        //Build the prompt with actual prediction data
        String prompt = buildPrompt(predictions, riskTolerance, principal, years);

        log.info("Sending portfolio optimization request to Gemini for {} funds", predictions.size());

        try {
            //Gemini API endpoint with API key as query parameter
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

            //Set up HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            //Build the request body for the Gemini API
            //Gemini uses a different format: contents -> parts -> text
            String systemInstruction = "You are a CAPM-based portfolio optimizer. You analyze mutual fund beta values and expected returns to suggest optimal allocations. You must include every fund that has valid data. You always respond with valid JSON only, no markdown formatting, no code fences.";

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    ),
                    "systemInstruction", Map.of(
                            "parts", List.of(
                                    Map.of("text", systemInstruction)
                            )
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.3
                    )
            );

            //Send the request to Gemini
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            String responseJson = restTemplate.postForObject(url, request, String.class);

            //Parse the Gemini response to get the content
            //Gemini response format: candidates[0].content.parts[0].text
            JsonNode root = objectMapper.readTree(responseJson);
            String content = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            log.info("Received Gemini response, parsing allocation data");

            //Strip markdown code fences if the AI wrapped the JSON in them
            content = stripCodeFences(content);

            //Parse the AI's JSON response into our recommendation model
            return parseRecommendation(content, predictions, riskTolerance, principal, years);

        } catch (Exception e) {
            log.error("Gemini API call failed: {}", e.getMessage(), e);
            throw new RuntimeException("AI portfolio optimization failed: " + e.getMessage());
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
        sb.append("- You MUST include EVERY fund that had a successful prediction. Do not exclude any fund. Even if a fund is less optimal, give it at least 5% allocation.\n");
        sb.append("- For CONSERVATIVE risk tolerance: Heavily favor funds with the LOWEST beta values. The lowest beta fund should get the highest allocation (up to 40%). Spread across all funds. Prioritize capital preservation and lower volatility.\n");
        sb.append("- For MODERATE risk tolerance: Balance between beta and expected return. Spread allocations fairly evenly across all funds with a slight tilt toward funds with the best risk-adjusted return (highest return relative to beta).\n");
        sb.append("- For AGGRESSIVE risk tolerance: Heavily favor funds with the HIGHEST beta and HIGHEST expected return. The highest beta fund should get the largest allocation (up to 50%). Prioritize maximum growth potential.\n");
        sb.append("- The allocations MUST be noticeably different between conservative, moderate, and aggressive. Do not give similar allocations for different risk levels.\n");

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
