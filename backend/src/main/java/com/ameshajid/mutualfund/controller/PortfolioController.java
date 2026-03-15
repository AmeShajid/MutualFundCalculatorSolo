/**
 PortfolioController handles the AI portfolio optimization endpoint.
 POST /api/portfolio/optimize - takes selected funds and risk tolerance,
 runs CAPM predictions in parallel, then sends data to Gemini for allocation advice.
 */
package com.ameshajid.mutualfund.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import com.ameshajid.mutualfund.model.PortfolioRequest;
import com.ameshajid.mutualfund.model.PortfolioChatRequest;
import com.ameshajid.mutualfund.model.PortfolioChatResponse;
import com.ameshajid.mutualfund.model.ComparisonResult;
import com.ameshajid.mutualfund.model.PredictionResponse;
import com.ameshajid.mutualfund.model.PortfolioRecommendation;
import com.ameshajid.mutualfund.service.PredictionService;
import com.ameshajid.mutualfund.service.FundService;
import com.ameshajid.mutualfund.service.GeminiService;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private static final Logger log = LoggerFactory.getLogger(PortfolioController.class);

    //Service that runs CAPM predictions for each fund
    private final PredictionService predictionService;

    //Service that looks up fund names by ticker symbol
    private final FundService fundService;

    //Service that calls Gemini for portfolio recommendations
    private final GeminiService geminiService;

    //Thread pool for running predictions in parallel
    private final ExecutorService executorService;

    //Valid risk tolerance values
    private static final List<String> VALID_RISK_TOLERANCES = List.of("conservative", "moderate", "aggressive");

    public PortfolioController(PredictionService predictionService, FundService fundService,
                               GeminiService geminiService, ExecutorService executorService) {
        this.predictionService = predictionService;
        this.fundService = fundService;
        this.geminiService = geminiService;
        this.executorService = executorService;
    }

    /**
     Optimizes a portfolio by running CAPM predictions then asking Gemini for allocation advice.
     */
    @PostMapping("/optimize")
    public ResponseEntity<?> optimize(@RequestBody PortfolioRequest request) {

        //Validate tickers
        if (request.getTickers() == null || request.getTickers().isEmpty()) {
            return ResponseEntity.badRequest().body(new PredictionController.ErrorResponse(
                    "Invalid input", "At least one ticker is required"));
        }

        //Clean and deduplicate tickers
        List<String> cleanTickers = request.getTickers().stream()
                .filter(t -> t != null && !t.trim().isEmpty())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());

        if (cleanTickers.isEmpty()) {
            return ResponseEntity.badRequest().body(new PredictionController.ErrorResponse(
                    "Invalid input", "At least one valid ticker is required"));
        }

        if (cleanTickers.size() > 5) {
            return ResponseEntity.badRequest().body(new PredictionController.ErrorResponse(
                    "Invalid input", "Maximum 5 funds allowed"));
        }

        //Validate principal
        if (request.getPrincipal() <= 0) {
            return ResponseEntity.badRequest().body(new PredictionController.ErrorResponse(
                    "Invalid input", "Principal must be > 0"));
        }

        //Validate years
        if (request.getYears() <= 0) {
            return ResponseEntity.badRequest().body(new PredictionController.ErrorResponse(
                    "Invalid input", "Years must be > 0"));
        }

        //Validate risk tolerance
        String riskTolerance = request.getRiskTolerance() != null ? request.getRiskTolerance().toLowerCase().trim() : "";
        if (!VALID_RISK_TOLERANCES.contains(riskTolerance)) {
            return ResponseEntity.badRequest().body(new PredictionController.ErrorResponse(
                    "Invalid input", "Risk tolerance must be conservative, moderate, or aggressive"));
        }

        log.info("Portfolio optimization requested for {} funds, risk={}, principal={}, years={}",
                cleanTickers.size(), riskTolerance, request.getPrincipal(), request.getYears());

        try {
            //Run predictions in parallel using CompletableFuture (same pattern as compare endpoint)
            List<CompletableFuture<ComparisonResult>> futures = cleanTickers.stream()
                    .map(ticker -> CompletableFuture.supplyAsync(() -> {
                        try {
                            String fundName = fundService.getFundName(ticker);
                            PredictionResponse prediction = predictionService.predict(
                                    ticker, request.getPrincipal(), request.getYears());
                            log.info("Portfolio prediction for {}: futureValue={}", ticker, prediction.getFutureValue());
                            return new ComparisonResult(ticker, fundName, prediction);
                        } catch (Exception e) {
                            log.error("Portfolio prediction failed for {}", ticker, e);
                            String fundName = fundService.getFundName(ticker);
                            return new ComparisonResult(ticker, fundName, e.getMessage());
                        }
                    }, executorService))
                    .collect(Collectors.toList());

            //Wait for all predictions to complete
            List<ComparisonResult> results = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            //Check if we have at least one successful prediction
            boolean hasSuccess = results.stream().anyMatch(r -> r.getPrediction() != null);
            if (!hasSuccess) {
                return ResponseEntity.status(503).body(new PredictionController.ErrorResponse(
                        "Service error", "All fund predictions failed. Cannot optimize portfolio."));
            }

            //Send predictions to Gemini for portfolio optimization
            PortfolioRecommendation recommendation = geminiService.getRecommendation(
                    results, riskTolerance, request.getPrincipal(), request.getYears());

            log.info("Portfolio optimization completed with {} allocations", recommendation.getAllocations().size());

            return ResponseEntity.ok(recommendation);

        } catch (Exception e) {
            log.error("Portfolio optimization failed", e);
            return ResponseEntity.status(503).body(new PredictionController.ErrorResponse(
                    "Service error", e.getMessage()));
        }
    }

    /**
     Handles follow-up chat questions about the portfolio recommendation.
     */
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody PortfolioChatRequest request) {

        if (request.getConversationHistory() == null || request.getConversationHistory().isEmpty()) {
            return ResponseEntity.badRequest().body(new PredictionController.ErrorResponse(
                    "Invalid input", "Conversation history is required"));
        }

        log.info("Portfolio chat request with {} messages", request.getConversationHistory().size());

        try {
            String systemPrompt = String.format(
                    "You are a CAPM-based portfolio advisor. The user is investing $%.2f over %.0f years " +
                    "with %s risk tolerance in these funds: %s. " +
                    "Answer follow-up questions about the portfolio you recommended. " +
                    "Be concise and helpful. Do not use markdown formatting.",
                    request.getPrincipal(), request.getYears(),
                    request.getRiskTolerance(),
                    String.join(", ", request.getTickers())
            );

            String reply = geminiService.chat(request.getConversationHistory(), systemPrompt);
            return ResponseEntity.ok(new PortfolioChatResponse(reply));

        } catch (Exception e) {
            log.error("Portfolio chat failed", e);
            return ResponseEntity.status(503).body(new PredictionController.ErrorResponse(
                    "Service error", e.getMessage()));
        }
    }
}
