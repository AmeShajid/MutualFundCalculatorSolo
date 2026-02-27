/**
This file starts the backend
 When run project, this file:
 Starts Spring Boot
 Sets up your server
 Scans for controllers, services, and configs
 Launches your backend application
 Without this file backend would not start.
 */
package com.ameshajid.mutualfund;
// This import allows us to start a Spring Boot application
import org.springframework.boot.SpringApplication;
// This import enables Spring Boot auto-configuration
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Main Spring Boot application
@SpringBootApplication
public class BackendApplication {

	//Main method
	public static void main(String[] args) {
		//starts up the spring boot backend
		SpringApplication.run(BackendApplication.class, args);
	}
}