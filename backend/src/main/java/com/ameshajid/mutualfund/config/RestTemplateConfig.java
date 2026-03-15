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
// This import marks a bean as the default when multiple beans of the same type exist
import org.springframework.context.annotation.Primary;
// This import lets us name a bean so we can inject it by name
import org.springframework.beans.factory.annotation.Qualifier;
// This import is the HTTP client we are configuring
import org.springframework.web.client.RestTemplate;
// This import lets us create an HTTP client factory with timeout settings
import org.springframework.http.client.SimpleClientHttpRequestFactory;

//This is so Spring knows this is a configuration class
@Configuration
public class RestTemplateConfig {

    //Creates a single RestTemplate bean shared across the entire application
    //Primary means this is the default RestTemplate used when no qualifier is specified
    @Bean
    @Primary
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

    //Creates a separate RestTemplate for Gemini API calls with longer read timeout
    //Gemini responses can take up to 30 seconds so we need a longer timeout
    @Bean
    @Qualifier("geminiRestTemplate")
    public RestTemplate geminiRestTemplate() {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        //Wait up to 5 seconds to establish a connection
        factory.setConnectTimeout(5000);

        //Wait up to 30 seconds for Gemini to respond
        factory.setReadTimeout(30000);

        return new RestTemplate(factory);
    }
}
