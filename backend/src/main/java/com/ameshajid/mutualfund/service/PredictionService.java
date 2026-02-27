package com.ameshajid.mutualfund.service;
// This tells Spring that this class is a service component
import org.springframework.stereotype.Service;
// This import allows us to return a PredictionResponse object
import com.ameshajid.mutualfund.model.PredictionResponse;

//This class as a service
@Service
public class PredictionService {
    //hardcoded risk free rate for formula
    private static final double RISK_FREE_RATE = 0.045;
    //fetching beta from newton beta
    private final NewtonBetaService newtonBetaService;
    //fetching historical return from yahoo
    private final YahooReturnService yahooReturnService;

    //Constructor allows spring to inject both services
    public PredictionService(NewtonBetaService newtonBetaService, YahooReturnService yahooReturnService) {
        this.newtonBetaService = newtonBetaService;
        this.yahooReturnService = yahooReturnService;
    }

    //Method for preforming full predicting calculation
    public PredictionResponse predict(String ticker, double principal, double years) {
        //calling newton api to get beta
        double beta = newtonBetaService.getBeta(ticker, "^GSPC", "1mo", 12);
        //calling yahoo service to get annual return
        double expectedReturn = yahooReturnService.getLastYearReturn(ticker);
        //formula CAPM
        double capmRate = RISK_FREE_RATE + beta * (expectedReturn - RISK_FREE_RATE);

        //Formula Continuous  compounding
        double futureValue = principal * Math.exp(capmRate * years);

        //return all packaged into PredictionResponse
        return new PredictionResponse(futureValue, beta, expectedReturn, RISK_FREE_RATE);
    }
}