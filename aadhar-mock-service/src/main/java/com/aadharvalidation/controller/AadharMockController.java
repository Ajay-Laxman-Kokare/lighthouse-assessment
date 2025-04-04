package com.aadharvalidation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aadharvalidation.model.AadharVerificationStatus;
import com.aadharvalidation.model.Ack;
import com.aadharvalidation.util.JwtUtil;

@RestController
@RequestMapping("/aadhar")
public class AadharMockController {
    /**
     * Validates an Aadhaar number.
     *@author ajaykokare
     * @param aadhaarNumber The 12-digit Aadhaar number.
     * @return A response entity with validation result.
     * return success and failure with ack, statuscode and error message
     */
	
	Logger logger = LoggerFactory.getLogger(AadharMockController.class);
	
	private final JwtUtil jwtUtil;
	
	public AadharMockController(JwtUtil jwtUtil) {
		super();
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/validateAadhaar")
	public ResponseEntity<Ack> validateAadhaar(@RequestHeader("Encrypted-Aadhaar") String encryptedAadhaar, @RequestHeader("Authorization")String token) {
		Ack validationResponse;
		if(!jwtUtil.validateToken(token.replace("Bearer ", ""))) {
			validationResponse = new Ack(AadharVerificationStatus.UNAUTHORIZED.getErrorCode(), false, AadharVerificationStatus.UNAUTHORIZED.getMessage());
			return ResponseEntity.ok(validationResponse);
		}
		
        try {
			String aadhaarNumber = JwtUtil.decryptAadhaar(encryptedAadhaar);
			
			validationResponse = validateAadhaarNumber(aadhaarNumber);

			if (!validationResponse.isSuccess()) {
				return ResponseEntity.ok(validationResponse);
			}

			return ResponseEntity.ok(validationResponse);
		} catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Ack(AadharVerificationStatus.DECRYPTION_ERROR.getErrorCode(), false, AadharVerificationStatus.DECRYPTION_ERROR.getMessage()));

		}
	}

	private Ack validateAadhaarNumber(String aadhaarNumber) {
		if (aadhaarNumber == null || aadhaarNumber.trim().isEmpty()) {
			return new Ack(AadharVerificationStatus.EMPTY.getErrorCode(), false, AadharVerificationStatus.EMPTY.getMessage());
		}

		if (aadhaarNumber.length() != 12) {
			return new Ack(AadharVerificationStatus.INVALID_LENGTH.getErrorCode(), false, AadharVerificationStatus.INVALID_LENGTH.getMessage());
		}

		if (!aadhaarNumber.matches("\\d{12}")) {
			return new Ack(AadharVerificationStatus.INVALID_FORMAT.getErrorCode(), false, AadharVerificationStatus.INVALID_FORMAT.getMessage());
		}

		return new Ack(AadharVerificationStatus.AADHAAR_VERIFIED.getErrorCode(), true, AadharVerificationStatus.AADHAAR_VERIFIED.getMessage());
	}
}
