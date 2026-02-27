package com.ameshajid.mutualfund.controller;
// This import allows us to use the Fund model class
import com.ameshajid.mutualfund.model.Fund;
// This import allows us to use the FundService class
import com.ameshajid.mutualfund.service.FundService;
// This import allows Spring to mark this class as a REST controller
import org.springframework.web.bind.annotation.RestController;
// This import allows us to define HTTP GET endpoints
import org.springframework.web.bind.annotation.GetMapping;
// This import allows us to define a base URL path for this controller
import org.springframework.web.bind.annotation.RequestMapping;
// This import allows us to inject dependencies into this class
import org.springframework.beans.factory.annotation.Autowired;
// This import allows us to return a list of objects
import java.util.List;

// This annotation tells Spring that this class is a REST controller
// It means this class will handle HTTP requests and return JSON responses
@RestController
// This sets the base URL path for all endpoints in this controller
// So all endpoints will start with "/api/funds"
@RequestMapping("/api/funds")
public class FundController {

    //creating var fundService
    private final FundService fundService;

    //Allows us to inject fundService automatically
    @Autowired
    public FundController(FundService fundService) {

        this.fundService = fundService;
    }

    // This defines a GET endpoint
        // When someone sends a GET request to "/api/funds",
    @GetMapping
    //returns a list of Fund objects
    public List<Fund> getAllFunds() {

        return fundService.getAllFunds();
    }
}