package com.lighthouse_exam.lighthouse_exam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer 
public class LighthouseExamApplication {

	public static void main(String[] args) {
		SpringApplication.run(LighthouseExamApplication.class, args);
	}

}
