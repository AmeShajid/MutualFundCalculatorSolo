/**
 RestTemplateConfig creates a single shared RestTemplate bean with timeouts.
 All services that need to make HTTP requests will use this same instance.
 The connection timeout controls how long to wait when establishing a connection.
 The read timeout controls how long to wait for a response after connecting.
 */
package com.ameshajid.mutualfund.config;

// This import lets us define a Spring bean
import org.springframework.context.annotation.Bean;
// This import marks this class as a configuration class
import org.springframework.context.annotation.Configuration;
// This import is the HTTP client we are configuring
import org.springframework.web.client.RestTemplate;
// This import lets us create an HTTP client factory with timeout settings
import org.springframework.http.client.SimpleClientHttpRequestFactory;

//This is so Spring knows this is a configuration class
@Configuration
public class RestTemplateConfig {

    //Creates a single RestTemplate bean shared across the entire application
    @Bean
    public RestTemplate restTemplate() {

        //Factory lets us configure connection and read timeouts
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        //Wait up to 5 seconds to establish a connection
        factory.setConnectTimeout(5000);

        //Wait up to 10 seconds to receive a response
        factory.setReadTimeout(10000);

        //Return a RestTemplate that uses our configured factory
        return new RestTemplate(factory);
    }
}
