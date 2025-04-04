package com.user.aadhar.Model;

public enum AadharResponseStatus {

    // Success Codes
    AADHAAR_VERIFIED("20001", "Aadhaar Verification Successful"),
    USER_CREATED("20002", "User successfully created"),

    // Error Codes
    INVALID_AUTH("40301", "Unauthorized: Invalid authentication credentials."),
    ACCESS_DENIED("40302", "Forbidden: You do not have permission to access this resource."),
    SERVICE_DOWN("50001", "Service Unavailable: The Aadhaar service is currently down."),
    BAD_REQUEST("40001", "Bad Request: Invalid request parameters."),
    UNKNOWN_ERROR("50002", "An unknown error occurred."),
    AADHAAR_INVALID("40002", "Aadhaar number is invalid or not found."),
    AUTHENTICATION_FAILED("40101", "Authentication failed."),
    DATABASE_ERROR("50003", "Database error while saving user."),
    INTERNAL_SERVER_ERROR("50001", "Service Unavailable: Internal Server Error"),
    TIMEOUT("40801", "Request timed out. Try again later."),
	REQLIMIT("42901", "Request Limit reached: 3 request per user allowed in a hour");

    private final String code;
    private final String message;

    AadharResponseStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
