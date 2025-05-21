package com.myobservation.fhir.service;

import java.io.IOException;

/**
 * Intefaz para cargar definiciones de estructura FHIR
 */
public interface StructureDefinitionLoader {
    /**
     * Carga una definición de estructura desde JSON
     * @param jsonProfile jsonProfile con el contenido del JSON Del perfilado del StructureDefinition
     */
    void loadStructureDefinition (String jsonProfile);

    /**
     * Carga el perfil JSON desde el classpath
     * @param resourcePath Ruta del recurso en el classpath
     * @return COntenido del perfil en formato JSON
     * @throws IOException Si ocurre error al leer un archivo
     */
    String loadJsonProfileFromClasspath (String resourcePath) throws IOException;
}
