package com.pja.bloodcount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BloodCountApplication {

	public static void main(String[] args) {
		SpringApplication.run(BloodCountApplication.class, args);
	}
}