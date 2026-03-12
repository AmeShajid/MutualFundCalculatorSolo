/**
 This controller handles prediction endpoints.
 GET /api/predict - single fund prediction
 GET /api/predict/compare - multiple fund comparison (parallel execution)

 Returns either:
 200 OK with the prediction, or
 400 Bad Request if input is invalid, or
 503 Service Unavailable if something fails (API/service error)
 */

package com.ameshajid.mutualfund.controller;
// This import gives us a logger to record events and errors
import org.slf4j.Logger;
// This import creates a logger instance for this class
import org.slf4j.LoggerFactory;
// This import lets us use ResponseEntity to return status codes
import org.springframework.http.ResponseEntity;
// This import lets us map a method to handle HTTP GET requests
import org.springframework.web.bind.annotation.GetMapping;
// This import lets Spring extract query parameters from the URL
import org.springframework.web.bind.annotation.RequestParam;
// This import lets us define a base URL path for this controller
import org.springframework.web.bind.annotation.RequestMapping;
// This import tells Spring this class is a REST API controller that returns JSON
import org.springframework.web.bind.annotation.RestController;
// These imports allow us to use List and ArrayList
import java.util.List;
import java.util.ArrayList;
// These imports allow us to use CompletableFuture for parallel execution
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
// This import allows us to collect stream results into a list
import java.util.stream.Collectors;
// This import brings in the PredictionResponse class (the result we send back)
import com.ameshajid.mutualfund.model.PredictionResponse;
// This import brings in the ComparisonResult class (one fund's result in a comparison)
import com.ameshajid.mutualfund.model.ComparisonResult;
// This import brings in the ComparisonResponse class (all funds' results together)
import com.ameshajid.mutualfund.model.ComparisonResponse;
// This import brings in the service class that does the actual prediction logic
import com.ameshajid.mutualfund.service.PredictionService;
// This import brings in the fund service to look up fund names
import com.ameshajid.mutualfund.service.FundService;


//Tells Spring this class is a REST controller
@RestController
// This sets the base route for this controller to "/api"
@RequestMapping("/api")
public class PredictionController {

    //Logger for recording events and errors in this controller
    private static final Logger log = LoggerFactory.getLogger(PredictionController.class);

    //reference to PredictionService which has our logic
    private final PredictionService predictionService;

    //reference to FundService to look up fund names by ticker
    private final FundService fundService;

    //reference to ExecutorService for running parallel predictions
    private final ExecutorService executorService;

    //constructor to inject all dependencies into this controller
    public PredictionController(PredictionService predictionService, FundService fundService, ExecutorService executorService) {
        this.predictionService = predictionService;
        this.fundService = fundService;
        this.executorService = executorService;
    }

    // This maps this method to GET requests at "/api/predict"
    @GetMapping("/predict")
    //Method for handling prediction req and returns success or error
    public ResponseEntity<?> predict(
            // Extract ticker from query parameter
            @RequestParam String ticker,
            // Extract principal from query parameter
            @RequestParam double principal,
            // Extract years from query parameter
            @RequestParam double years) {

        //Using try block for errors
        try {
            //check if the ticker is empty
            if (ticker == null || ticker.trim().isEmpty()) {
                //returning error
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        //error type
                        "Invalid input",
                        //error message
                        "Ticker is required"));
            }

            //check if principal greater than 0
            if (principal <= 0) {
                //returning error
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        //error type
                        "Invalid input",
                        //error message
                        "Principal must be > 0"));
            }

            //checks if years are valid
            if (years <= 0) {

                //returning error
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        //error type
                        "Invalid input",
                        //error message
                        "Years must be > 0"));
            }

            //calculating prediction
            PredictionResponse response = predictionService.predict(
                    //sending ticker
                    ticker,
                    //sending principal
                    principal,
                    //sending years
                    years);

            log.info("Prediction for {}: futureValue={}", ticker, response.getFutureValue());

            // returns OK response + prediction
            return ResponseEntity.ok(response);

            //for any other error
        } catch (Exception e) {

            log.error("Prediction failed for ticker={}, principal={}, years={}", ticker, principal, years, e);

            //Service error
            return ResponseEntity.status(503).body(new ErrorResponse(
                    //error type
                    "Service error",
                    //exception message
                     e.getMessage()));
        }
    }

    // This maps this method to GET requests at "/api/predict/compare"
    @GetMapping("/predict/compare")
    //Method for comparing multiple funds in parallel
    public ResponseEntity<?> compare(
            // Extract list of tickers from query parameters (e.g., ?tickers=VSMPX&tickers=FXAIX)
            @RequestParam List<String> tickers,
            // Extract principal from query parameter
            @RequestParam double principal,
            // Extract years from query parameter
            @RequestParam double years) {

        //Filter out blank tickers and deduplicate
        List<String> cleanTickers = tickers.stream()
                .filter(t -> t != null && !t.trim().isEmpty())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());

        //Validate that at least one ticker was provided
        if (cleanTickers.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    "Invalid input",
                    "At least one ticker is required"));
        }

        //Validate max 5 funds for comparison
        if (cleanTickers.size() > 5) {
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    "Invalid input",
                    "Maximum 5 funds allowed for comparison"));
        }

        //Validate principal
        if (principal <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    "Invalid input",
                    "Principal must be > 0"));
        }

        //Validate years
        if (years <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    "Invalid input",
                    "Years must be > 0"));
        }

        log.info("Comparison requested for {} funds: {}", cleanTickers.size(), cleanTickers);

        //Run each fund's prediction in parallel using CompletableFuture
        List<CompletableFuture<ComparisonResult>> futures = cleanTickers.stream()
                .map(ticker -> CompletableFuture.supplyAsync(() -> {
                    try {
                        //Look up the fund name for display
                        String fundName = fundService.getFundName(ticker);
                        //Run the prediction using existing logic
                        PredictionResponse prediction = predictionService.predict(ticker, principal, years);
                        log.info("Comparison prediction for {}: futureValue={}", ticker, prediction.getFutureValue());
                        //Return successful result
                        return new ComparisonResult(ticker, fundName, prediction);
                    } catch (Exception e) {
                        //Log the error and return a failed result for this fund
                        log.error("Comparison prediction failed for {}", ticker, e);
                        String fundName = fundService.getFundName(ticker);
                        return new ComparisonResult(ticker, fundName, e.getMessage());
                    }
                }, executorService))
                .collect(Collectors.toList());

        //Wait for all predictions to complete and collect results
        List<ComparisonResult> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        //Return all results wrapped in a ComparisonResponse
        return ResponseEntity.ok(new ComparisonResponse(results, principal, years));
    }

    //Helper class for returning error json responses
    public static class ErrorResponse {

        //this is for the error category
        private String error;

        //this is for the error message
        private String message;

        //constructor for error
        public ErrorResponse(String error, String message) {

            this.error = error;
            this.message = message;
        }

        //Getter so spring can read the error
        public String getError() {

            return error;
        }

        //Getter so spring can read the message
        public String getMessage() {

            return message;
        }
    }
}