package com.pja.bloodcount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BloodCountApplication {

	public static void main(String[] args) {
		SpringApplication.run(BloodCountApplication.class, args);
	}

	//TODO: add swagger
	//TODO: add tests
	//TODO: ensure current user can access only his data/resources/apis
	//TODO: add CORS configuration
}
