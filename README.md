# 🏥 MyObservation - Plataforma de Gestión de Observaciones Clínicas Caso de Uso Pacientes con Hipertensión

**MyObservation** es una aplicación monolítica modular desarrollada con el stack Spring Boot, enfocada en la interoperabilidad clínica mediante estándares abiertos como **FHIR** y **openEHR**. Permite registrar, almacenar y consultar observaciones clínicas estructuradas, respetando los principios de interoperabilidad semántica y sintáctica.

---

## ⚙️ Stack Tecnológico

| Componente          | Elección                                                                 |
|---------------------|--------------------------------------------------------------------------|
| Backend             | Spring Boot (arquitectura monolítica modular)                           |
| Seguridad           | Spring Security (con JWT o autenticación basada en roles)               |
| Interoperabilidad   | HL7® FHIR (con HAPI FHIR) y SUSHI (para definición de perfiles FHIR)     |
| Datos clínicos      | openEHR (modelado con arquetipos y plantillas)                          |
| Persistencia        | PostgreSQL (almacenamiento estructurado y JSONB para datos clínicos)    |
| Infraestructura     | Sin servidor dedicado de FHIR ni EHRbase (ligero, enfocado y modular)   |

---

## 📦 Características

- 📋 Registro de observaciones clínicas según recursos FHIR (`Observation`, `Patient`, etc.)
- 🔒 Seguridad con Spring Security (JWT o básica)
- 🧠 Modelado semántico con arquetipos y plantillas openEHR (sin motor EHR)
- 🧬 Conversión entre FHIR ↔ JSON ↔ modelos clínicos
- 🔎 Consulta eficiente de datos en PostgreSQL con campos JSONB
- 🔧 Modularidad para separar capas (dominio, infraestructura, presentación)

---

## 📁 Estructura del Proyecto (modular)


