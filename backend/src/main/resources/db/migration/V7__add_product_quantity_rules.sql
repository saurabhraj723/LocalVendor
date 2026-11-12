ALTER TABLE products
    ADD COLUMN min_quantity NUMERIC(12, 3) NOT NULL DEFAULT 1,
    ADD COLUMN quantity_step NUMERIC(12, 3) NOT NULL DEFAULT 1;

ALTER TABLE products
    ADD CONSTRAINT chk_products_min_quantity_positive CHECK (min_quantity > 0),
    ADD CONSTRAINT chk_products_quantity_step_positive CHECK (quantity_step > 0);
