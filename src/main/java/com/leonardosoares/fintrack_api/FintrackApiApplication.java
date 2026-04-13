package com.leonardosoares.fintrack_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FintrackApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FintrackApiApplication.class, args);
	}

}