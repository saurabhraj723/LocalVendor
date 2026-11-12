ALTER TABLE orders DROP CONSTRAINT chk_orders_status;

UPDATE orders SET status = 'PACKING' WHERE status = 'PREPARING';
UPDATE order_status_history SET old_status = 'PACKING' WHERE old_status = 'PREPARING';
UPDATE order_status_history SET new_status = 'PACKING' WHERE new_status = 'PREPARING';

ALTER TABLE orders
    ADD CONSTRAINT chk_orders_status CHECK (status IN (
        'NEW', 'CONFIRMED', 'REJECTED', 'PACKING', 'READY',
        'OUT_FOR_DELIVERY', 'PICKUP_READY', 'DELIVERED', 'COMPLETED', 'CANCELLED'
    ));
