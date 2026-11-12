ALTER TABLE orders
    ADD COLUMN whatsapp_message_id VARCHAR(160);

CREATE UNIQUE INDEX uq_orders_business_whatsapp_message
    ON orders (business_id, whatsapp_message_id)
    WHERE whatsapp_message_id IS NOT NULL;
