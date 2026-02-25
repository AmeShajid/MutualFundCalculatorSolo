package com.ameshajid.mutualfund.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ameshajid.mutualfund.model.FutureValueResponse;
import com.ameshajid.mutualfund.service.FutureValueService;

@RestController
@RequestMapping("/api")
public class FutureValueController {

    private final FutureValueService futureValueService;

    public FutureValueController(FutureValueService futureValueService) {
        this.futureValueService = futureValueService;
    }

    @GetMapping("/future-value")
    public Object getFutureValue(
            @RequestParam String symbol,
            @RequestParam double principal,
            @RequestParam int years
    ) {

        if (symbol == null || symbol.trim().isEmpty()) {
            return "{\"status\":400,\"message\":\"symbol is required\"}";
        }

        if (principal <= 0) {
            return "{\"status\":400,\"message\":\"principal must be > 0\"}";
        }

        if (years <= 0) {
            return "{\"status\":400,\"message\":\"years must be > 0\"}";
        }

        try {
            FutureValueResponse response =
                    futureValueService.calculateFutureValue(symbol.trim(), principal, years);

            return response;

        } catch (Exception e) {
            return "{\"status\":500,\"message\":\"" + e.getMessage() + "\"}";
        }
    }
}