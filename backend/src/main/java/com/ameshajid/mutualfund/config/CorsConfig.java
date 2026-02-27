package com.ameshajid.mutualfund.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//this is so springboot knows this is a conf
@Configuration
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