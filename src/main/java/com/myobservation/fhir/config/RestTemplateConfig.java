package com.myobservation.fhir.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración de RestTemplate para realizar solicitudes HTTP externas
 * Define un Bean de Spring para crear una instancia única de 'RestTemplate1
 * que se podrá inyectar en otros componentes para consumir APIs externas
 */
@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate es un cliente HTTP que permite enviar y recibir datos de APIs REST
     * @return instancia de 'RestTemplate'
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
