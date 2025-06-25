# 🏥 MyObservation - Plataforma de Gestión de Observaciones Clínicas Caso de Uso Pacientes con Hipertensión

**MyObservation** es una aplicación con arquitectura monolítica modular desarrollada en Spring Boot, nuestro backend se convierte en un middleware que interactua con un frontend y servidores clínicos como Aidbox con FHIR y EHRBase con OpenEHR, de esta manera se logra persistir/intercambiar tanto recursos FHIR como Composiciones en OpenEHR.
Enfocada en la interoperabilidad clínica mediante estándares abiertos como **FHIR** y **openEHR**. Permite registrar, almacenar y consultar observaciones clínicas estructuradas, respetando los principios de interoperabilidad semántica y sintáctica.
De esta manera tenemos una aplicación web hibrida con dominio web (JWT para sesiones) + dominio clínico (FHIR/OPENEHR).

---

## ⚙️ Stack Tecnológico

| Componente          | Elección                                                                 |
|---------------------|--------------------------------------------------------------------------|
| Backend             | Spring Boot (arquitectura monolítica modular, actua como middleware)                           |
| Seguridad           | Spring Security (con JWT, autenticación basada en roles)               |
| Interoperabilidad   | HL7® FHIR (con HAPI FHIR) y SUSHI (para definición de perfiles FHIR)
| Interoperabilidad     OpenEHR (con EHRBASE+.opt) + BetterCare Arquetype Designer.
| Persistencia        | PostgreSQL (almacenamiento estructurado para el dominio web)    |
| Infraestructura     | Orquestación de contenedores con Docker   |

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


