package com.user.aadhar.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AadharResponse {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    private String message;
    private boolean success;
    private String statusCode;

   
    public AadharResponse(String message, boolean success, String statusCode) {
        this.message = message;
        this.success = success;
        this.statusCode = statusCode;
    }

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

    // Utility Method to Check Failure
    public boolean isFailure() {
        return !this.success;
    }

	@Override
	public String toString() {
		return "AadharResponse [id=" + id + ", message=" + message + ", success=" + success + ", statusCode="
				+ statusCode + "]";
	}
    
}
