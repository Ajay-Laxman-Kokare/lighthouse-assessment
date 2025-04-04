package com.aadharvalidation.model;

public enum AadharVerificationStatus {
    AADHAAR_VERIFIED("SUC200", "Aadhaar verification successful"),
    EMPTY("ERR001", "Aadhaar number cannot be empty"),
    INVALID_LENGTH("ERR002", "Aadhaar number must be exactly 12 digits"),
    INVALID_FORMAT("ERR003", "Aadhaar number must contain only digits"),
	UNAUTHORIZED("ERR401","Request is not authorized"),
    DECRYPTION_ERROR("ERR004","Error decrypting Aadhaar number"),
    INTERNAL_SERVER_ERROR("50001", "Service Unavailable: Internal Server Error");



    private final String errorCode;
    private final String message;

    AadharVerificationStatus(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}

