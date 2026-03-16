/**
 Configures CORS for backend.
 Allows the frontend to make HTTP requests to the Spring Boot backend.
 The allowed origin is loaded from application.properties so it works in both dev and production.
 */

package com.ameshajid.mutualfund.config;
// This import allows us to mark this class as a configuration class in Spring
import org.springframework.context.annotation.Configuration;
// This import allows us to inject values from application.properties
import org.springframework.beans.factory.annotation.Value;
// This import gives us access to CorsRegistry, which is used to define CORS rules
import org.springframework.web.servlet.config.annotation.CorsRegistry;
// This import allows us to customize Spring MVC configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//this is so springboot knows this is a conf
@Configuration
//Implements WebMvcConfigurer so we can override its methods
public class CorsConfig implements WebMvcConfigurer {

    //Loads the allowed origin from application.properties
    //Defaults to localhost:4200 for local development
    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Override

    //method to define CORS rules for app
    public void addCorsMappings(CorsRegistry registry) {
        // - "/api/**" means apply these rules to all endpoints that start with /api/
        registry.addMapping("/api/**")
                // - allowedOrigins uses the value from application.properties
                .allowedOrigins(allowedOrigin)
                // - allowedMethods(...) allows these HTTP methods to be used
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // - allowedHeaders("*") allows all request headers
                .allowedHeaders("*");
    }
}