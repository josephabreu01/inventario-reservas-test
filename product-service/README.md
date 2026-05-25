# Servicio de Productos (`product-service`)

Este microservicio gestiona el catálogo de productos y el historial de precios. Está construido con **Java 21** y **Quarkus 3.x**, siguiendo los principios de la **Arquitectura Hexagonal**.

## 🛠️ Tecnologías y Características
- **Base de Datos**: PostgreSQL para el almacenamiento persistente de productos e historial de cambios de precios.
- **Caché (Redis)**: Almacena en caché los tipos de cambio de divisas para optimizar las consultas multidivisa.
- **Mensajería**: Publica eventos de ciclo de vida del producto (`PRODUCT_CREATED`, `PRODUCT_DELETED`) a un bróker Kafka.
- **Seguridad**: Rutas protegidas mediante Keycloak JWT tokens con control de acceso basado en roles (`Admin`, `User`).

## 🚀 Ejecutar en Modo de Desarrollo
Para ejecutar la aplicación localmente en modo live-coding:

```bash
./mvnw compile quarkus:dev
```

El panel interactivo de administración Dev UI estará disponible en: <http://localhost:8080/q/dev/>.

## 📦 Empaquetado y Construcción
Para empaquetar la aplicación en un archivo JAR ejecutable:

```bash
./mvnw package
```

Esto genera el ejecutable `quarkus-run.jar` en la carpeta `target/quarkus-app/`. Puedes ejecutarlo con:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## 🎯 Ejecución de Pruebas
Ejecuta las pruebas unitarias y de integración del servicio:

```bash
mvn test
```
