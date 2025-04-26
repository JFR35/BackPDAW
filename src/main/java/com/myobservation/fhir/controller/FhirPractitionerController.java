package com.myobservation.fhir.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import com.myobservation.auth.entity.MyUser;
import com.myobservation.auth.repository.MyUserRepository;
import com.myobservation.fhir.fhir.FhirPractitionerEntity;
import com.myobservation.fhir.persistence.FhirPractitionerRepository;
import org.hl7.fhir.r4.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/practitioners")
public class FhirPractitionerController {

    private static final Logger log = LoggerFactory.getLogger(FhirPractitionerController.class);

    private final FhirContext fhirContext;
    private final FhirValidator fhirValidator;
    private final FhirPractitionerRepository fhirPractitionerRepository;
    private final MyUserRepository userRepository;
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
    public ResponseEntity<Map<String, ?>> createPractitioner(@RequestBody String practitionerJson) {
        log.info("Received practitioner JSON: {}", practitionerJson);
        try {
            if (practitionerJson == null || practitionerJson.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Empty JSON"));
            }

            Practitioner practitioner = jsonParser.parseResource(Practitioner.class, practitionerJson);
            ValidationResult result = fhirValidator.validateWithResult(practitioner);
            if (result.isSuccessful()) {
                // Add meta.profile after validation to reference the custom profile
                practitioner.getMeta().addProfile("http://myhealthapp.org/fhir/StructureDefinition/mi-practitioner-persistencia");

                FhirPractitionerEntity entity = new FhirPractitionerEntity();
                // Store the updated JSON with meta.profile
                entity.setResourcePractitionerJson(jsonParser.encodeResourceToString(practitioner));

                Long userId = getCurrentUserId();
                if (userId == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "User not authenticated"));
                }
                Optional<MyUser> myUserOptional = userRepository.findById(userId);

                if (myUserOptional.isPresent()) {
                    entity.setUser(myUserOptional.get());
                    FhirPractitionerEntity savedEntity = fhirPractitionerRepository.save(entity);

                    // Asignar ID lógico
                    practitioner.setId("Practitioner/" + savedEntity.getId());
                    String updatedJson = jsonParser.encodeResourceToString(practitioner);
                    savedEntity.setResourcePractitionerJson(updatedJson);
                    fhirPractitionerRepository.save(savedEntity);

                    return ResponseEntity.status(HttpStatus.CREATED)
                            .header("Location", "/fhir/Practitioner/" + savedEntity.getId())
                            .body(Collections.singletonMap("id", "Practitioner/" + savedEntity.getId()));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "User not found"));
                }
            } else {
                List<String> errors = result.getMessages().stream()
                        .map(msg -> String.format("Validation error at %s: %s", msg.getLocationString(), msg.getMessage()))
                        .collect(Collectors.toList());
                log.warn("Validation failed for Practitioner: {}", errors);
                return ResponseEntity.badRequest().body(Collections.singletonMap("errors", errors));
            }
        } catch (Exception e) {
            log.error("Error creating practitioner", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Error: " + e.getMessage()));
        }
    }
    // Ejemplo de método para obtener el userId del contexto (si usas Spring Security):
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MyUser) {
            MyUser user = (MyUser) authentication.getPrincipal();
            return user.getUserId();
        }
        return null;
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, ?>> getPractitioner(@PathVariable Long id) {
        try {
            Optional<FhirPractitionerEntity> entityOptional = fhirPractitionerRepository.findById(id);
            if (entityOptional.isPresent()) {
                String practitionerJson = entityOptional.get().getResourcePractitionerJson();
                return ResponseEntity.ok(Collections.singletonMap("resource", practitionerJson));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Practitioner not found"));
            }
        } catch (Exception e) {
            log.error("Error retrieving Practitioner", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error: " + e.getMessage()));
        }
    }
    @GetMapping
    public ResponseEntity<Map<String, ?>> getAllPractitioners() {
        try {
            List<FhirPractitionerEntity> practitioners = fhirPractitionerRepository.findAll();
            List<String> practitionerJsons = practitioners.stream()
                    .map(FhirPractitionerEntity::getResourcePractitionerJson)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Collections.singletonMap("resources", practitionerJsons));
        } catch (Exception e) {
            log.error("Error retrieving all Practitioners", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para busqueda por identifier
     * @param identifier
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, ?>> searchPractitioners(@RequestParam String identifier) {
        try {
            List<FhirPractitionerEntity> practitioners = fhirPractitionerRepository.findByIdentifierContaining(identifier);
            List<String> practitionerJsons = practitioners.stream()
                    .map(FhirPractitionerEntity::getResourcePractitionerJson)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Collections.singletonMap("resources", practitionerJsons));
        } catch (Exception e) {
            log.error("Error searching Practitioners", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error: " + e.getMessage()));
        }
    }
}