CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL REFERENCES businesses(id),
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    order_number VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'NEW',
    subtotal NUMERIC(12, 2) NOT NULL,
    delivery_charge NUMERIC(12, 2) NOT NULL DEFAULT 0,
    discount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    tax NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_status VARCHAR(24) NOT NULL DEFAULT 'UNPAID',
    source VARCHAR(24) NOT NULL DEFAULT 'VENDOR_APP',
    notes VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_orders_business_number UNIQUE (business_id, order_number),
    CONSTRAINT chk_orders_status CHECK (status IN ('NEW', 'CONFIRMED', 'REJECTED', 'PREPARING', 'READY', 'OUT_FOR_DELIVERY', 'PICKUP_READY', 'DELIVERED', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_orders_payment_status CHECK (payment_status IN ('UNPAID', 'PARTIALLY_PAID', 'PAID', 'REFUNDED')),
    CONSTRAINT chk_orders_amounts_nonnegative CHECK (subtotal >= 0 AND delivery_charge >= 0 AND discount >= 0 AND tax >= 0 AND total_amount >= 0)
);

CREATE INDEX idx_orders_business_created ON orders(business_id, created_at DESC);
CREATE INDEX idx_orders_business_status_created ON orders(business_id, status, created_at DESC);
CREATE INDEX idx_orders_customer_created ON orders(customer_id, created_at DESC);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    product_name VARCHAR(160) NOT NULL,
    unit VARCHAR(32) NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    quantity NUMERIC(12, 3) NOT NULL,
    line_total NUMERIC(12, 2) NOT NULL,
    CONSTRAINT chk_order_items_values CHECK (unit_price >= 0 AND quantity > 0 AND line_total >= 0)
);

CREATE INDEX idx_order_items_order ON order_items(order_id);

CREATE TABLE order_status_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    old_status VARCHAR(32),
    new_status VARCHAR(32) NOT NULL,
    changed_by BIGINT NOT NULL REFERENCES users(id),
    changed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    notes VARCHAR(500)
);

CREATE INDEX idx_order_status_history_order_time ON order_status_history(order_id, changed_at);
