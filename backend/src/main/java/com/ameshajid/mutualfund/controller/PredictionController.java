/**
 This controller creates the POST /api/predict endpoint.
 It:
 Takes the user’s input (ticker, principal, years)
 Validates it (rejects bad input)
 Calls PredictionService to calculate the prediction

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
// This import brings in the PredictionResponse class (the result we send back)
import com.ameshajid.mutualfund.model.PredictionResponse;
// This import brings in the service class that does the actual prediction logic
import com.ameshajid.mutualfund.service.PredictionService;


//Tells Spring this class is a REST controller
@RestController
// This sets the base route for this controller to "/api"
@RequestMapping("/api")
public class PredictionController {

    //Logger for recording events and errors in this controller
    private static final Logger log = LoggerFactory.getLogger(PredictionController.class);

    //reference to PredictionService which has our logic
    private final PredictionService predictionService;

    //constructor to inject PredictionService into this controller
    public PredictionController(PredictionService predictionService) {

        this.predictionService = predictionService;
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