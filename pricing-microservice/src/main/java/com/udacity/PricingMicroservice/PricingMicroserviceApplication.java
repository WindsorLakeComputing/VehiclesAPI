package com.udacity.PricingMicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Creates a Spring Boot Application to run the Pricing Service.
 * TODO: Convert the application from a REST API to a microservice.
 */
@SpringBootApplication
@EnableEurekaClient
public class PricingMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PricingMicroserviceApplication.class, args);
    }

}
