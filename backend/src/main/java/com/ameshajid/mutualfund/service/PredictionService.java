/**
 PredictionService is the core logic layer.
 Calls Newton API to get beta
 Calls Yahoo service to get expected return
 Calculates the CAPM rate
 Calculates the future value using continuous compounding
 Returns all results in a PredictionResponse
 This is where the actual finance math happens.
 */
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

    //Method for performing the full prediction calculation
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
        PredictionResponse response = new PredictionResponse(futureValue, beta, expectedReturn, RISK_FREE_RATE);

        //Check if this fund has near-zero beta and return (likely a money market fund)
        //CAPM is not reliable for these funds because they earn yield through interest/dividends
        //rather than price appreciation, which is what Yahoo Finance tracks
        if (Math.abs(beta) < 0.01 && Math.abs(expectedReturn) < 0.01) {
            response.setWarning("This fund appears to be a money market or stable-value fund. "
                    + "CAPM is not reliable for this fund type because it earns returns through "
                    + "interest/dividends rather than price movement. The projection shown only "
                    + "reflects the risk-free rate and may not be accurate.");
        }

        return response;
    }
}