package com.brilliantmule.servicedesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Service Desk API Spring Boot application.
 * <p>
 * Bootstraps the Spring context and starts the embedded web server.
 */
@SpringBootApplication
public class ServiceDeskApiApplication {

	/**
	 * Starts the Service Desk API application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(ServiceDeskApiApplication.class, args);
	}

}
