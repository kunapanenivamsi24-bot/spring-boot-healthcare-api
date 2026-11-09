package com.healthcare.api.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException patient(Long id) {
        return new ResourceNotFoundException("Patient not found with id: " + id);
    }

    public static ResourceNotFoundException appointment(Long id) {
        return new ResourceNotFoundException("Appointment not found with id: " + id);
    }
}
