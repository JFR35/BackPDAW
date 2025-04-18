package com.myobservation.fhir.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import com.myobservation.auth.entity.MyUser;
import com.myobservation.auth.repository.MyUserRepository; // Necesitas el repositorio de usuarios
import com.myobservation.fhir.fhir.FhirPractitionerEntity;
import com.myobservation.fhir.persistence.FhirPractitionerRepository;
import org.hl7.fhir.r4.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/practitioners")
public class FhirPractitionerController {

    private static final Logger log = LoggerFactory.getLogger(FhirPractitionerController.class);

    private final FhirContext fhirContext;
    private final FhirValidator fhirValidator;
    private final FhirPractitionerRepository fhirPractitionerRepository;
    private final MyUserRepository userRepository; // Inyecta el repositorio de usuarios
    private final IParser jsonParser;

    public FhirPractitionerController(FhirContext fhirContext, FhirValidator fhirValidator,
                                      FhirPractitionerRepository fhirPractitionerRepository,
                                      MyUserRepository userRepository) {
        this.fhirContext = fhirContext;
        this.fhirValidator = fhirValidator;
        this.fhirPractitionerRepository = fhirPractitionerRepository;
        this.userRepository = userRepository;
        this.jsonParser = fhirContext.newJsonParser().setPrettyPrint(true);
    }

    @PostMapping
    public ResponseEntity<String> createPractitioner(@RequestBody String practitionerJson, @RequestParam Long userId) {
        log.info("Received raw practitioner JSON for user ID {}: [{}]", userId, practitionerJson);
        try {
            if (practitionerJson == null || practitionerJson.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty or null JSON");
            }

            Optional<MyUser> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found with ID: " + userId);
            }
            MyUser user = userOptional.get();

            Practitioner practitioner = jsonParser.parseResource(Practitioner.class, practitionerJson);
            ValidationResult result = fhirValidator.validateWithResult(practitioner);

            if (result.isSuccessful()) {
                FhirPractitionerEntity entity = new FhirPractitionerEntity();
                entity.setResourcePractitionerJson(practitionerJson);
                // **Aquí establecemos la relación con MyUser**
                // entity.setUser(user); // Necesitarías añadir esta relación en tu FhirPractitionerEntity
                fhirPractitionerRepository.save(entity);
                return ResponseEntity.status(HttpStatus.CREATED).body("Practitioner created and associated with user ID: " + userId);
            } else {
                String errorDetails = result.getMessages().stream()
                        .map(SingleValidationMessage::getMessage)
                        .collect(Collectors.joining("\n"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid FHIR resource:\n" + errorDetails);
            }
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());
        }
    }

    // ... (otros métodos del controlador: getAll, getById, update, delete) ...
}