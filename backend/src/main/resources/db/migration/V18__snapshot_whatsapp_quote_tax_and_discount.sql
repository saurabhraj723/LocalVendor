ALTER TABLE whatsapp_test_cart_items
    ADD COLUMN quoted_discount_percent NUMERIC(5, 2),
    ADD COLUMN quoted_tax_type VARCHAR(24),
    ADD COLUMN quoted_tax_percent NUMERIC(5, 2);

UPDATE whatsapp_test_cart_items cart_item
SET quoted_discount_percent = product.discount_percent,
    quoted_tax_type = product.tax_type,
    quoted_tax_percent = product.tax_percent
FROM products product
WHERE product.id = cart_item.product_id;
