package com.skilltrack.ai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(resourceType + " with identifier " + identifier + " not found");
    }

    public ResourceNotFoundException(String resourceType, Object identifier) {
        super(resourceType + " with identifier " + identifier + " not found");
    }
}