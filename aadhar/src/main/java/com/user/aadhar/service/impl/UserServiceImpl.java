package com.user.aadhar.service.impl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.user.aadhar.Model.Utility;
import com.user.aadhar.repository.UserRepo;
import com.user.aadhar.Model.AadharResponse;
import com.user.aadhar.Model.AadharResponseStatus;
import com.user.aadhar.Model.User;
import com.user.aadhar.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private UserRepo userRepo;

    @Value("${aadhar.mock.url}")
    private String aadharMockUrl;
	

	@Value("${user.auth.login}")
	private String loginUrl;
	
    private String AUTH_TOKEN = "Bearer "; 
	
    @Override
    public User save(User user) {
        AadharResponse aadhaarResponse; 
        ResponseEntity<AadharResponse> response = null;
        if (!isAuthenticatedUser(user)) {
            aadhaarResponse = new AadharResponse(AadharResponseStatus.AUTHENTICATION_FAILED.getMessage(), 
                                                 false, 
                                                 AadharResponseStatus.AUTHENTICATION_FAILED.getCode());
            user.setResponse(aadhaarResponse);
            return saveUser(user);
        }

        try {
            String encryptedAadhar = Utility.encryptAadhaar(user.getAadharNumber());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Encrypted-Aadhaar", encryptedAadhar);
            headers.set("Authorization", AUTH_TOKEN);  
            //reset token to default value 
            AUTH_TOKEN = "Bearer ";
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            String url = "http://localhost:9091" + aadharMockUrl;

            response = restTemplate.postForEntity(url, requestEntity, AadharResponse.class);
            
            if (response != null && response.getBody() != null) {
                aadhaarResponse = response.getBody();
            } else {
                aadhaarResponse = new AadharResponse(AadharResponseStatus.UNKNOWN_ERROR.getMessage(), 
                                                     false, 
                                                     AadharResponseStatus.UNKNOWN_ERROR.getCode());
            }
            
            if (aadhaarResponse.isSuccess()) {
                /*aadhaarResponse = new AadharResponse(AadharResponseStatus.AADHAAR_VERIFIED.getMessage(),
                                                     true,
                                                     AadharResponseStatus.AADHAAR_VERIFIED.getCode());*/
            	aadhaarResponse = new AadharResponse(response.getBody().getMessage(), true, response.getBody().getStatusCode());
            } else {
                /*aadhaarResponse = new AadharResponse(AadharResponseStatus.AADHAAR_INVALID.getMessage(),
                                                     false,
                                                     AadharResponseStatus.AADHAAR_INVALID.getCode());*/
            	aadhaarResponse = new AadharResponse(response.getBody().getMessage(), false, response.getBody().getStatusCode());

            }

            String maskedAadhar = "XXXX-XXXX-" + user.getAadharNumber().substring(8);
            user.setAadharNumber(maskedAadhar);
            user.setResponse(aadhaarResponse);

        } catch (HttpClientErrorException.Forbidden e) {
            logger.error("Forbidden access: " + e.getMessage(), e);
            aadhaarResponse = new AadharResponse(AadharResponseStatus.ACCESS_DENIED.getMessage(), 
                                                 false, 
                                                 AadharResponseStatus.ACCESS_DENIED.getCode());
            user.setResponse(aadhaarResponse);

        } catch (HttpClientErrorException.BadRequest e) {
            logger.error("Bad request: " + e.getMessage(), e);
            
        	aadhaarResponse = new AadharResponse(e.getMessage(), false, e.getStatusCode().toString());

            user.setResponse(aadhaarResponse);

        } catch (ResourceAccessException e) { 
            logger.error("Timeout error: " + e.getMessage(), e);
            aadhaarResponse = new AadharResponse(AadharResponseStatus.TIMEOUT.getMessage(), 
                                                 false, 
                                                 AadharResponseStatus.TIMEOUT.getCode());
            user.setResponse(aadhaarResponse);

        } catch (Exception e) {
            logger.error("Unexpected error: " + e.getMessage(), e);
            aadhaarResponse = new AadharResponse(AadharResponseStatus.SERVICE_DOWN.getMessage(), 
                                                 false, 
                                                 AadharResponseStatus.SERVICE_DOWN.getCode());
            user.setResponse(aadhaarResponse);
        }

        return saveUser(user);
    }

    private User saveUser(User user) {
        try {
            return userRepo.save(user);
        } catch (Exception e) {
            logger.error("Database error while saving user: " + e.getMessage(), e);
            user.setResponse(new AadharResponse(AadharResponseStatus.DATABASE_ERROR.getMessage(), 
                                                false, 
                                                AadharResponseStatus.DATABASE_ERROR.getCode()));
            return user;
        }
    }

    public boolean isAuthenticatedUser(User user) {

		String url = "http://localhost:9091" + loginUrl + "?username=" + user.getName() + "&password="
				+ user.getPassword();

		ResponseEntity<String> response = null;
		try {
			response = restTemplate.postForEntity(url, null, String.class);
		} catch (RestClientException e) {
			logger.error(AadharResponseStatus.SERVICE_DOWN.getMessage(), e);
			// return
			// ResponseEntity.internalServerError().body(AadharResponseErrors.SERVICEDOWN.toString());
			return false;
		}

		if (response != null && response.getStatusCode() == HttpStatus.OK) {
			// String token = response.getBody();
			if ("invalid".equals(response.getBody())) {
				logger.error(AadharResponseStatus.INVALID_AUTH.getMessage());
				// return
				// ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AadharResponseErrors.INVALID.toString());
				return false;
			}
		}
		AUTH_TOKEN += response.getBody();
		return true;
	}
    }
