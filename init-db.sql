-- =============================================================
-- Inventrio — Script de inicialización de bases de datos
-- Ejecutado automáticamente por PostgreSQL en el primer arranque
-- del contenedor Docker (montado en /docker-entrypoint-initdb.d/)
--
-- Nota: Hibernate (database.generation=update) sincroniza el
-- esquema en cada arranque. Este script sirve como referencia
-- completa del esquema y garantiza la creación en arranques limpios.
-- =============================================================

-- -------------------------------------------------------------
-- Crear bases de datos
-- -------------------------------------------------------------
CREATE DATABASE product_db;
CREATE DATABASE inventory_db;


-- =============================================================
-- PRODUCT DATABASE
-- =============================================================
\connect product_db;

-- Tabla de productos
CREATE TABLE IF NOT EXISTS product (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(255)    NOT NULL,
    description VARCHAR(255),
    price       NUMERIC(19, 2)  NOT NULL,
    category    VARCHAR(255),
    sku         VARCHAR(255)    NOT NULL UNIQUE
);

-- Historial de precios (se registra cada cambio de precio)
CREATE TABLE IF NOT EXISTS price_history (
    id          BIGSERIAL       PRIMARY KEY,
    product_id  BIGINT          NOT NULL REFERENCES product(id),
    price       NUMERIC(19, 2)  NOT NULL,
    changed_at  TIMESTAMP       NOT NULL
);

-- Filtro por categoría (evita full table scan en findByCategory)
CREATE INDEX IF NOT EXISTS idx_product_category ON product(category);

-- Composite: cubre WHERE product_id = ? ORDER BY changed_at DESC en una sola pasada
CREATE INDEX IF NOT EXISTS idx_price_history_product_date ON price_history(product_id, changed_at DESC);


-- =============================================================
-- INVENTORY DATABASE
-- =============================================================
\connect inventory_db;

-- Inventario por producto
-- product_name, product_price, product_category: réplica local
-- sincronizada via eventos Kafka (Event-Carried State Transfer)
CREATE TABLE IF NOT EXISTS inventory (
    id               BIGSERIAL       PRIMARY KEY,
    product_id       BIGINT          NOT NULL UNIQUE,
    quantity         INTEGER         NOT NULL DEFAULT 0,
    product_name     VARCHAR(255),
    product_price    NUMERIC(19, 2),
    product_category VARCHAR(255),
    current_stock  INTEGER         NOT NULL DEFAULT 0
    );

-- Historial de movimientos de inventario (ENTRY / EXIT)
CREATE TABLE IF NOT EXISTS inventory_movement (
    id              BIGSERIAL       PRIMARY KEY,
    product_id      BIGINT          NOT NULL,
    quantity_change INTEGER         NOT NULL,
    movement_type   VARCHAR(10)     NOT NULL,
    created_at      TIMESTAMP       NOT NULL
);

-- Composite: cubre WHERE product_id = ? ORDER BY created_at DESC en una sola pasada
CREATE INDEX IF NOT EXISTS idx_inventory_movement_product_date ON inventory_movement(product_id, created_at DESC);

-- Control de idempotencia: eventos Kafka ya procesados
CREATE TABLE IF NOT EXISTS processed_event (
    event_id        VARCHAR(255)    PRIMARY KEY,
    processed_at    TIMESTAMP       NOT NULL
);
