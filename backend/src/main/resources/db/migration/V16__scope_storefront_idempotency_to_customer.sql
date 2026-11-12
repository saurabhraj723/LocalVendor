DROP INDEX IF EXISTS uq_orders_business_storefront_request;

CREATE UNIQUE INDEX uq_orders_business_customer_storefront_request
    ON orders (business_id, customer_id, storefront_request_id)
    WHERE storefront_request_id IS NOT NULL;
