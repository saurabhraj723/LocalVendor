ALTER TABLE products
    ADD COLUMN discount_percent NUMERIC(5, 2) NOT NULL DEFAULT 0,
    ADD COLUMN tax_type VARCHAR(24),
    ADD COLUMN tax_percent NUMERIC(5, 2),
    ADD CONSTRAINT chk_products_discount_percent CHECK (discount_percent >= 0 AND discount_percent <= 100),
    ADD CONSTRAINT chk_products_tax_configuration CHECK (
        (tax_type IS NULL AND tax_percent IS NULL)
        OR (tax_type IN ('GST', 'VAT', 'OTHER') AND tax_percent IS NOT NULL AND tax_percent > 0 AND tax_percent <= 100)
    );

ALTER TABLE order_items
    ADD COLUMN discount_percent NUMERIC(5, 2) NOT NULL DEFAULT 0,
    ADD COLUMN discount_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    ADD COLUMN tax_type VARCHAR(24),
    ADD COLUMN tax_percent NUMERIC(5, 2),
    ADD COLUMN tax_amount NUMERIC(12, 2) NOT NULL DEFAULT 0;
