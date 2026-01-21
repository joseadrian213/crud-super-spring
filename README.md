# API Backend - Sistema de Gestión de Supermercado

Este proyecto es una API RESTful desarrollada con Spring Boot para la gestión integral de un supermercado. Está diseñado no solo para cumplir con requerimientos funcionales de CRUD (Crear, Leer, Actualizar, Borrar) de productos, ventas y usuarios, sino también para demostrar **buenas prácticas de arquitectura**, **robustez** y un **entendimiento profundo** del framework.

## 🚀 Filosofía de Diseño y Puntos Destacados

El código ha sido estructurado pensando en un perfil **Junior avanzado**, priorizando la **intencionalidad** sobre la "magia" automática de las librerías en ciertos puntos clave:

### 1. Robustez y Programación Defensiva 🛡️
Se ha implementado una estrategia de **Doble Validación**:
*   **Capa Web (Controller)**: Validación de entrada usando `@Valid` y Bean Validation para asegurar la integridad de la petición HTTP.
*   **Capa de Negocio (Service)**: Validaciones explícitas de lógica de negocio y nulidad dentro de los Servicios.
    *   *¿Por qué?*: Esto garantiza que la integridad de los datos se mantenga incluso si el Servicio es invocado desde otro lugar que no sea un Controller (ej. tests, procesos batch, otros servicios), siguiendo el principio de que **la capa de dominio debe autoprotegerse**.

### 2. Implementación Explícita vs Automática 🧠
Se ha optado deliberadamente por implementaciones manuales en áreas como el Mapeo de Objetos (DTOs) y el Manejo de Excepciones:
*   **Mappers Manuales**: En lugar de depender ciegamente de librerías como MapStruct o ModelMapper, se han construido mappers manuales.
    *   *Objetivo*: Demostrar el conocimiento de cómo fluyen y se transforman los datos entre capas, manteniendo un control total sobre la performance y evitando la "caja negra" de la autogeneración de código.
*   **Manejo de Errores**: Uso de `RestControllerAdvice` combinando respuestas tipadas (`ErrorResponseDTO`) con estructuras dinámicas (`Map`) para mostrar versatilidad en el manejo de diferentes escenarios de error.

### 3. Arquitectura Limpia y Seguridad 🔐
*   **Arquitectura de N-Capas**: Separación estricta entre `Controller`, `Service`, `Repository` y `Model`.
*   **Seguridad con JWT**: Implementación completa de Spring Security con autenticación y autorización basada en Roles (ADMIN, USER) usando Json Web Tokens.
*   **DTO Pattern**: Los modelos de base de datos nunca se exponen directamente; toda la comunicación externa es vía Data Transfer Objects.

---

## 🛠️ Stack Tecnológico

*   **Lenguaje**: Java 17
*   **Framework**: Spring Boot 3.x
*   **Base de Datos**: H2 (en memoria para desarrollo/test) / Compatible con MySQL.
*   **Seguridad**: Spring Security 6 + JJWT (0.13.0).
*   **Persistencia**: Spring Data JPA + Hibernate.
*   **Herramientas**: Lombok, Maven.
*   **Reportes**:
    *   OpenHTMLtoPDF (Generación de Tickets PDF).
    *   Apache POI (Reportes en Excel).

---

## 📋 Funcionalidades Principales

*   **Autenticación**: Registro y Login de usuarios con roles.
*   **Gestión de Productos**: Alta, baja (lógica), modificación y consulta.
*   **Gestión de Sucursales**: Administración de puntos de venta.
*   **Ventas**:
    *   Registro de ventas con detalle.
    *   Cálculo automático de totales.
    *   Generación de Tickets en PDF.
    *   Exportación de reportes de ventas a Excel.

## 📦 Instalación y Ejecución

1.  **Clonar el repositorio**:
    ```bash
    git clone https://github.com/tu-usuario/ejercicio_supermercado.git
    ```
2.  **Compilar el proyecto**:
    ```bash
    ./mvnw clean install
    ```
3.  **Ejecutar**:
    ```bash
    ./mvnw spring-boot:run
    ```
4.  **Acceso a la API**:
    La aplicación iniciará por defecto en el puerto `8080`.
    *   H2 Console: `http://localhost:8080/h2-console`
    *   Swagger/OpenAPI (si estuviera configurado): `http://localhost:8080/swagger-ui.html`

## 🧪 Notas para Reclutadores / Revisores

Este repositorio busca equilibrar la simplicidad necesaria para una prueba técnica con la profundidad requerida para entornos productivos reales. Se invita a revisar los **Services** para ver la lógica de validación defensiva y la clase `Mapper` para la transformación de datos.
