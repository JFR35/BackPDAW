package com.myobservation.ehrbridge.config;

import com.myobservation.ehrbridge.service.EhrBaseService;
import org.ehrbase.openehr.sdk.client.openehrclient.OpenEhrClient;
import org.ehrbase.openehr.sdk.client.openehrclient.OpenEhrClientConfig;
import org.ehrbase.openehr.sdk.client.openehrclient.defaultrestclient.DefaultRestClient;
import org.ehrbase.openehr.sdk.webtemplate.templateprovider.FileBasedTemplateProvider;
import org.ehrbase.openehr.sdk.webtemplate.templateprovider.TemplateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.net.URI;

/**
 * Clase para configurar la integración con EHRbase proporcionando el acceso y econexión con el cliente EHRbase
 */
@Configuration
public class EhrBaseConfig {

    // Logger para depuración
    private static final Logger logger = LoggerFactory.getLogger(EhrBaseService.class);
    // Variables para credenciales  definidas en properties, aún no se aplica ningún gestor de secretos
    @Value("${ehrbase.url}")
    private String ehrBaseUrl;

    @Value("${ehrbase.username}")
    private String ehrBaseUsername;

    @Value("${ehrbase.password}")
    private String ehrBasePassword;

    @Value("${ehrbase.templates.path}")
    private String templatePath;

    // Carga los archivos de los templates en mi path de resources
    @Bean
    public TemplateProvider templateProvider() {
        File templateDirectory = new File(templatePath);
        logger.info("Verificando archivos en: {}", templatePath);
        for (File file : templateDirectory.listFiles()) {
            logger.info("Encontrado: {}", file.getName());
        }
        if (!templateDirectory.exists() || !templateDirectory.isDirectory()) {
            throw new RuntimeException("Invalid template directory: " + templatePath);
        }
        FileBasedTemplateProvider provider = new FileBasedTemplateProvider(templateDirectory.toPath());
        logger.info("Templates loaded: {}", provider.find(templatePath));
        return provider;
    }

    // Inicializa el cliente EHRbase en la URL definida en las variables
    @Bean
    public OpenEhrClient openEhrClient(TemplateProvider templateProvider) {
        try {
            OpenEhrClientConfig config = new OpenEhrClientConfig(URI.create(ehrBaseUrl));
            return new DefaultRestClient(config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize EHR client", e);
        }
    }
}