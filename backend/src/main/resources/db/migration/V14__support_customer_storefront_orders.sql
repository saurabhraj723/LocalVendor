ALTER TABLE order_status_history
    ALTER COLUMN changed_by DROP NOT NULL;

ALTER TABLE orders
    ADD COLUMN storefront_request_id VARCHAR(80);

CREATE UNIQUE INDEX uq_orders_business_storefront_request
    ON orders (business_id, storefront_request_id)
    WHERE storefront_request_id IS NOT NULL;
