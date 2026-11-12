CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL REFERENCES businesses(id),
    category_id BIGINT REFERENCES categories(id),
    name VARCHAR(160) NOT NULL,
    description VARCHAR(1000),
    sku VARCHAR(80),
    unit VARCHAR(32) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_products_price_nonnegative CHECK (price >= 0)
);

CREATE INDEX idx_products_business_active_name ON products(business_id, is_active, name);
CREATE INDEX idx_products_business_category ON products(business_id, category_id);
CREATE UNIQUE INDEX uq_products_business_sku ON products(business_id, lower(sku)) WHERE sku IS NOT NULL;
