package com.aadharvalidation.model;

public class Ack {
    private String message;
    private boolean success;
    private String statusCode;
    
    public Ack(String message, boolean success, String statusCode) {
        this.message = message;
        this.success = success;
        this.statusCode = statusCode;
    }

    // Constructor for Errors (With Error Code)
    public Ack(AadharVerificationStatus error, String statusCode) {
        this.message = error.getMessage();
        this.success = false;
        this.statusCode = statusCode;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

}
