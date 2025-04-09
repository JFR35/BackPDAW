# BackPDAW
Estructura del Carpetas
src/main/java/com/tuapp/
│
├── auth/              <-- Módulo de autenticación
│   ├── controller/
│   ├── service/
│   ├── dto/
│   └── config/
│
├── user/              <-- Gestión de usuarios
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── security/          <-- JWT, filtros, configuración
│   ├── jwt/
│   ├── config/
│   └── util/
│
├── fhir/              <-- Lógica FHIR (clínica)
│   ├── controller/
│   ├── service/
│   └── model/
│
├── openehr/           <-- Lógica OpenEHR
│   ├── adapter/
│   ├── service/
│   └── model/
│
├── common/            <-- Excepciones, utilidades, validadores
└── BooksRestApplication.java
