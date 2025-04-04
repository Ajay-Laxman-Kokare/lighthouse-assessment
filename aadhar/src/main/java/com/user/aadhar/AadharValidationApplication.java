package com.user.aadhar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AadharValidationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AadharValidationApplication.class, args);
	}

}
