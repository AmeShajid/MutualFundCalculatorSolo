/**
 ExecutorConfig creates a dedicated thread pool for running parallel API calls.
 When comparing multiple funds, each fund's prediction runs in its own thread.
 We use a fixed thread pool instead of the default ForkJoinPool because our tasks
 are I/O-bound (waiting on external APIs), not CPU-bound.
 */
package com.ameshajid.mutualfund.config;

//Allows us to use ExecutorService for managing threads
import java.util.concurrent.ExecutorService;
//Allows us to create thread pools
import java.util.concurrent.Executors;
//This import lets us define a Spring bean
import org.springframework.context.annotation.Bean;
//This import marks this class as a configuration class
import org.springframework.context.annotation.Configuration;

//This is so Spring knows this is a configuration class
@Configuration
public class ExecutorConfig {

    //Creates a fixed thread pool with 10 threads for parallel API calls
    //10 threads supports 5 funds x 2 API calls each (Newton + Yahoo)
    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }
}
