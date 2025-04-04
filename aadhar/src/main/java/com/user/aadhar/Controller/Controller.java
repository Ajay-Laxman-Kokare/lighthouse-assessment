package com.user.aadhar.Controller;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.user.aadhar.Model.AadharResponse;
import com.user.aadhar.Model.AadharResponseStatus;
import com.user.aadhar.Model.User;
import com.user.aadhar.service.UserService;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

@RestController
@RequestMapping("/user")
public class Controller {
	 
	Logger logger = LoggerFactory.getLogger(Controller.class);
	
    private RateLimiterRegistry rateLimiterRegistry;

	
	@Autowired
	UserService userService;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${user.auth.register}")
	String registrationUrl;
	
    public Controller() {
        this.rateLimiterRegistry = RateLimiterRegistry.ofDefaults();
    }

		
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestParam String username, @RequestParam String password) {
	    
	    String url = "http://localhost:9091" + registrationUrl + "?username=" + username + "&password=" + password;
	    
	    try {
	        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

	        if (response.getStatusCode().is2xxSuccessful()) {
	            return ResponseEntity.ok("User "+username+ " registered successfully");
	        } else {
	            return ResponseEntity.status(response.getStatusCode())
	                                 .body("User registration failed");
	        }

	    } catch (RestClientException e) {
	        logger.error("Service Down: " + e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Aadhaar Service is currently unavailable");
	    }
	}

	
	@PostMapping("/validation")
    public ResponseEntity<User> validateAadharNumber(@RequestBody User user) {
		
		//String key = user.getName()+"_"+user.getAadharNumber();
		
        io.github.resilience4j.ratelimiter.RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(user.getName(), createRateLimiterConfig());

        try {
            return rateLimiter.executeSupplier(() -> {
                User savedUser = userService.save(user);
                savedUser.setPassword("XXXXXXXXX");
                return ResponseEntity.ok(savedUser);
            });
        } catch (Exception e) {
            user.setPassword("XXXXXXXXX");
            return rateLimitFallback(user);
        }
    }

    public ResponseEntity<User> rateLimitFallback(User user) {
        AadharResponse errorResponse = new AadharResponse(
            AadharResponseStatus.REQLIMIT.getMessage(), false,
            AadharResponseStatus.REQLIMIT.getCode()
        );
        user.setResponse(errorResponse);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(user);
    }

    private RateLimiterConfig createRateLimiterConfig() {
        return RateLimiterConfig.custom()
            .limitForPeriod(300)
            .limitRefreshPeriod(Duration.ofHours(1))
            .timeoutDuration(Duration.ZERO)
            .build();
    }
	
}
