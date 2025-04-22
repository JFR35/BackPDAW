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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

            // Parsear el JSON recibido
            Practitioner practitioner = jsonParser.parseResource(Practitioner.class, practitionerJson);
            ValidationResult result = fhirValidator.validateWithResult(practitioner);

            if (result.isSuccessful()) {
                // Crear la entidad de FhirPractitioner
                FhirPractitionerEntity entity = new FhirPractitionerEntity();
                entity.setResourcePractitionerJson(practitionerJson);

                // Obtener el userId del contexto de seguridad
                Long userId = getCurrentUserId();

                Optional<MyUser> myUserOptional = userRepository.findById(userId);

                if (myUserOptional.isPresent()) {
                    MyUser user = myUserOptional.get();
                    entity.setUser(user);

                    // Guardar la entidad
                    fhirPractitionerRepository.save(entity);

                    // **Devolver directamente el userId del MyUser asociado**
                    return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("userId", userId));
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "User not found"));
                }
            } else {
                // Recopilar los mensajes de error de validación
                List<String> errors = result.getMessages().stream()
                        .map(SingleValidationMessage::getMessage)
                        .collect(Collectors.toList());
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
}