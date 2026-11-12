CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL REFERENCES businesses(id),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_categories_business_order ON categories(business_id, sort_order, name);
CREATE UNIQUE INDEX uq_categories_business_name ON categories(business_id, lower(name));
