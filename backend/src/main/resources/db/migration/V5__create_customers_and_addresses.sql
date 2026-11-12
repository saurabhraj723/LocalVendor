CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL REFERENCES businesses(id),
    name VARCHAR(160) NOT NULL,
    phone VARCHAR(32),
    email VARCHAR(254),
    notes VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_customers_business_name ON customers(business_id, name);
CREATE INDEX idx_customers_business_phone ON customers(business_id, phone);

CREATE TABLE customer_addresses (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    label VARCHAR(60) NOT NULL,
    recipient_name VARCHAR(160),
    phone VARCHAR(32),
    address_line1 VARCHAR(240) NOT NULL,
    address_line2 VARCHAR(240),
    locality VARCHAR(160),
    city VARCHAR(120) NOT NULL,
    state VARCHAR(120),
    postal_code VARCHAR(24),
    country_code CHAR(2) NOT NULL DEFAULT 'IN',
    delivery_instructions VARCHAR(500),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_customer_addresses_customer_order ON customer_addresses(customer_id, sort_order);
CREATE UNIQUE INDEX uq_customer_addresses_single_default ON customer_addresses(customer_id) WHERE is_default = TRUE;
