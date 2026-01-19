package com.lineagehub.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String resource, UUID id) {
        super(String.format("%s với ID %s không tồn tại", resource, id));
    }
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
