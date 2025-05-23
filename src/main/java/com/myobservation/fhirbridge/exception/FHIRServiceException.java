package com.myobservation.fhirbridge.exception;

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
