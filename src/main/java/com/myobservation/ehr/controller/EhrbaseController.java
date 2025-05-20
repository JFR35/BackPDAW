package com.myobservation.ehr.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ehrbase")
public class EhrbaseController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String EHRBASE_URL = "http://localhost:8089/ehrbase/rest/openehr/v1";

    @PostMapping("/crear-ehr")
    public ResponseEntity<String> crearEhr() {
        String url = EHRBASE_URL + "/ehr";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // EHRBase might need authorization headers depending on your setup
        // headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes()));

        HttpEntity<String> request = new HttpEntity<>("{}", headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return ResponseEntity.status(response.getStatusCode()).body("EHR creado: " + response.getBody());
    }

    @PostMapping("/enviar-composicion")
    public ResponseEntity<String> enviarComposicion(@RequestParam String ehrId) {
        String templateId = "Presión Sanguínea"; // Exact match with template name in EHRBase
        String url = EHRBASE_URL + "/composition";

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("ehrId", ehrId)
                .queryParam("templateId", templateId)
                .queryParam("format", "FLAT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Add authorization headers if needed
        // headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes()));

        Map<String, Object> composition = new HashMap<>();

        // Context fields (mandatory)
        composition.put("ctx/language", "es");
        composition.put("ctx/territory", "ES");
        composition.put("ctx/composer_name", "Dr. Backend");
        composition.put("ctx/time", "2025-05-20T10:00:00+02:00");
        composition.put("ctx/id_scheme", "id_scheme");
        composition.put("ctx/id_namespace", "id_namespace");
        composition.put("ctx/health_care_facility|name", "Hospital Local");
        composition.put("ctx/health_care_facility|id", UUID.randomUUID().toString());

        // Report fields - based on your PresionSanguineaComposition class
        composition.put("presion_sanguinea/context/report_id|value", "BP-" + UUID.randomUUID().toString().substring(0, 8));
        composition.put("presion_sanguinea/context/status|value", "final");

        // Blood pressure fields - based on BloodPressureObservation class structure
        // Time of observation
        composition.put("presion_sanguinea/blood_pressure/any_event:0/time", "2025-05-20T10:00:00+02:00");

        // Systolic & Diastolic values
        composition.put("presion_sanguinea/blood_pressure/any_event:0/systolic|magnitude", 120);
        composition.put("presion_sanguinea/blood_pressure/any_event:0/systolic|unit", "mm[Hg]");
        composition.put("presion_sanguinea/blood_pressure/any_event:0/diastolic|magnitude", 80);
        composition.put("presion_sanguinea/blood_pressure/any_event:0/diastolic|unit", "mm[Hg]");

        // Optional protocol elements - not strictly required but may help match the model
        composition.put("presion_sanguinea/blood_pressure/method|code", "at1036");
        composition.put("presion_sanguinea/blood_pressure/method|value", "Auscultation");
        composition.put("presion_sanguinea/blood_pressure/method|terminology", "local");

        // Location of measurement
        composition.put("presion_sanguinea/blood_pressure/location_of_measurement|code", "at0033");
        composition.put("presion_sanguinea/blood_pressure/location_of_measurement|value", "Right arm");
        composition.put("presion_sanguinea/blood_pressure/location_of_measurement|terminology", "local");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(composition, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    builder.toUriString(),
                    request,
                    String.class
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error enviando composición: " + e.getMessage());
        }
    }

    @GetMapping("/buscar-composiciones")
    public ResponseEntity<String> buscarComposiciones(@RequestParam String ehrId) {
        String url = EHRBASE_URL + "/ehr/" + ehrId + "/composition";

        HttpHeaders headers = new HttpHeaders();
        // Add authorization headers if needed

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar composiciones: " + e.getMessage());
        }
    }

    @GetMapping("/obtener-composicion")
    public ResponseEntity<String> obtenerComposicion(
            @RequestParam String ehrId,
            @RequestParam String compositionId) {
        String url = EHRBASE_URL + "/ehr/" + ehrId + "/composition/" + compositionId;

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("format", "FLAT");

        HttpHeaders headers = new HttpHeaders();
        // Add authorization headers if needed

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener composición: " + e.getMessage());
        }
    }
}