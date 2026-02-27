/**
 Configures CORS for backend.
 It allows Angular frontend (running on http://localhost:4200) to make HTTP requests to Spring Boot backend.
 Without this the browser would block your frontend from calling your backend.
 */

package com.ameshajid.mutualfund.config;
// This import allows us to mark this class as a configuration class in Spring
import org.springframework.context.annotation.Configuration;
// This import gives us access to CorsRegistry, which is used to define CORS rules
import org.springframework.web.servlet.config.annotation.CorsRegistry;
// This import allows us to customize Spring MVC configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//this is so springboot knows this is a conf
@Configuration
//Implements WebMvcConfigurer so we can override its methods
public class CorsConfig implements WebMvcConfigurer {
    @Override

    //method to define CORS rules for app
    public void addCorsMappings(CorsRegistry registry) {
        // - "/api/**" means apply these rules to all endpoints that start with /api/
        registry.addMapping("/api/**")
                // - allowedOrigins("http://localhost:4200") allows Angular (running on port 4200) to access backend
                .allowedOrigins("http://localhost:4200")
                // - allowedMethods(...) allows these HTTP methods to be used
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // - allowedHeaders("*") allows all request headers
                .allowedHeaders("*");
    }
}