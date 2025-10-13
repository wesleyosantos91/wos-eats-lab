CREATE SCHEMA IF NOT EXISTS catalog_schema;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE kitchen
(
    id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE restaurant
(
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name         VARCHAR(120)   NOT NULL,
    kitchen_id   UUID          NOT NULL REFERENCES kitchen (id),
    delivery_fee NUMERIC(10, 2) NOT NULL,
    active       BOOLEAN        NOT NULL DEFAULT true
);

CREATE TABLE product
(
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    restaurant_id UUID           NOT NULL REFERENCES restaurant (id),
    name          VARCHAR(120)   NOT NULL,
    description   TEXT,
    price         NUMERIC(12, 2) NOT NULL,
    active        BOOLEAN        NOT NULL DEFAULT true,
    image_key     VARCHAR(255)
);
