ALTER TABLE businesses
    ADD COLUMN storefront_slug VARCHAR(48),
    ADD COLUMN customer_registration_code VARCHAR(32);

UPDATE businesses
SET storefront_slug = LOWER(SUBSTRING(REPLACE(gen_random_uuid()::text, '-', '') FROM 1 FOR 32)),
    customer_registration_code = UPPER(SUBSTRING(REPLACE(gen_random_uuid()::text, '-', '') FROM 1 FOR 32));

ALTER TABLE businesses
    ALTER COLUMN storefront_slug SET NOT NULL,
    ALTER COLUMN customer_registration_code SET NOT NULL;

CREATE UNIQUE INDEX uq_businesses_storefront_slug ON businesses (storefront_slug);
CREATE UNIQUE INDEX uq_businesses_customer_registration_code ON businesses (customer_registration_code);

ALTER TABLE customers ADD COLUMN password_hash VARCHAR(100);

CREATE INDEX ix_customers_business_email ON customers (business_id, LOWER(email))
    WHERE email IS NOT NULL;

CREATE UNIQUE INDEX uq_customers_portal_business_email ON customers (business_id, LOWER(email))
    WHERE password_hash IS NOT NULL;
