package com.ameshajid.mutualfund.controller;
// This import lets us use ResponseEntity to return status codes
import org.springframework.http.ResponseEntity;
// This import lets us map a method to handle HTTP POST requests
import org.springframework.web.bind.annotation.PostMapping;
// This import lets Spring convert incoming JSON into a Java object
import org.springframework.web.bind.annotation.RequestBody;
// This import lets us define a base URL path for this controller
import org.springframework.web.bind.annotation.RequestMapping;
// This import tells Spring this class is a REST API controller that returns JSON
import org.springframework.web.bind.annotation.RestController;
// This import brings in the PredictionRequest class (the input from the frontend)
import com.ameshajid.mutualfund.model.PredictionRequest;
// This import brings in the PredictionResponse class (the result we send back)
import com.ameshajid.mutualfund.model.PredictionResponse;
// This import brings in the service class that does the actual prediction logic
import com.ameshajid.mutualfund.service.PredictionService;


//Tells Spring this class is a REST controller
@RestController
// This sets the base route for this controller to "/api"
@RequestMapping("/api")
public class PredictionController {

    //reference to PredictionService which has our logic
    private final PredictionService predictionService;

    //constructor to inject PredictionService into this controller
    public PredictionController(PredictionService predictionService) {

        this.predictionService = predictionService;
    }

    // This maps this method to POST requests at "/api/predict"
    @PostMapping("/predict")
    //Method for handling prediction req and returns success or error
    public ResponseEntity<?> predict(@RequestBody PredictionRequest request) {

        //Using try block for errors
        try {
            //check if the ticker is empty
            if (request.getTicker() == null || request.getTicker().trim().isEmpty()) {
                //returning error
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        //error type
                        "Invalid input",
                        //error message
                        "Ticker is required"));
            }

            //check if principal greater than 0
            if (request.getPrincipal() <= 0) {
                //returning error
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        //error type
                        "Invalid input",
                        //error message
                        "Principal must be > 0"));
            }

            //checks if years are valid
            if (request.getYears() <= 0) {

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
                    request.getTicker(),
                    //sending principal
                    request.getPrincipal(),
                    //sending years
                    request.getYears()
            );

            // returns OK response + prediction
            return ResponseEntity.ok(response);

            //for any other error
        } catch (Exception e) {

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