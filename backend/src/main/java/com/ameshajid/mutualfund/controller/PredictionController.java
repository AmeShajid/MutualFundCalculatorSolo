package com.ameshajid.mutualfund.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ameshajid.mutualfund.model.PredictionRequest;
import com.ameshajid.mutualfund.model.PredictionResponse;
import com.ameshajid.mutualfund.service.PredictionService;

@RestController
@RequestMapping("/api")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/predict")
    public ResponseEntity<?> predict(@RequestBody PredictionRequest request) {
        try {
            if (request.getTicker() == null || request.getTicker().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        "Invalid input",
                        "Ticker is required"
                ));
            }

            if (request.getPrincipal() <= 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        "Invalid input",
                        "Principal must be > 0"
                ));
            }

            if (request.getYears() <= 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse(
                        "Invalid input",
                        "Years must be > 0"
                ));
            }

            PredictionResponse response = predictionService.predict(
                    request.getTicker(),
                    request.getPrincipal(),
                    request.getYears()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(503).body(new ErrorResponse(
                    "Service error",
                    e.getMessage()
            ));
        }
    }

    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() { return error; }
        public String getMessage() { return message; }
    }
}
