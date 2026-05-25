# Servicio de Inventarios (`inventory-service`)

Este microservicio gestiona los niveles de stock físico, los movimientos e historial de stock, y consume los eventos de sincronización del catálogo. Está construido con **Java 21** y **Quarkus 3.x**, siguiendo los principios de la **Arquitectura Hexagonal**.

## 🛠️ Tecnologías y Características
- **Base de Datos**: PostgreSQL para el almacenamiento persistente de niveles de stock y registros de movimientos.
- **Idempotencia**: Implementa un **Consumidor Idempotente** registrando los IDs de los eventos procesados en la tabla `processed_event` para prevenir inconsistencias por retransmisión de mensajes.
- **Mensajería**: Consume eventos asíncronos (`PRODUCT_CREATED`, `PRODUCT_DELETED`) de Kafka para sincronizar automáticamente el inventario de nuevos productos.
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
