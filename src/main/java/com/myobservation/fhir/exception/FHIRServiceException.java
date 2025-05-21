package com.myobservation.fhir.exception;

import com.myobservation.fhir.service.FHIRValidationService;

/**
 * Excepciones custom para errores relacionados con FHIR
 */
public class FHIRServiceException extends RuntimeException {
    public FHIRServiceException (String message) {
        super(message);
    }
    public FHIRServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
