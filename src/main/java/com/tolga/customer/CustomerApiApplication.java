package com.tolga.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan; // Added for package scanning
import org.springframework.scheduling.annotation.EnableScheduling; // Added for the clock engine
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling // 1. Activates the background cron job scheduler core
@ComponentScan(basePackages = {"com.tolga.customer", "com.tolga.myapplication2.cron"}) // 2. Tells Spring to find your new download job file
public class CustomerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerApiApplication.class, args);
	}

	// CEVDET2: Exposing RestTemplate bean context so Spring can autowire it to controllers
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}