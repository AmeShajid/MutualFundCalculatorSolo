package com.ameshajid.mutualfund.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ameshajid.mutualfund.service.NewtonBetaService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class BetaController {

    private final NewtonBetaService newtonBetaService;

    public BetaController(NewtonBetaService newtonBetaService) {
        this.newtonBetaService = newtonBetaService;
    }

    @GetMapping(value = "/beta", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getBeta(
            @RequestParam String ticker,
            @RequestParam(defaultValue = "^GSPC") String index,
            @RequestParam(defaultValue = "1mo") String interval,
            @RequestParam(defaultValue = "10") Integer observations
    ) {
        String json = newtonBetaService.getBeta(ticker, index, interval, observations);
        return ResponseEntity.ok(json);
    }
}
