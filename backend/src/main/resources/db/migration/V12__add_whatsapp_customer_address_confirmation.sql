ALTER TABLE whatsapp_test_conversations
    ADD COLUMN selected_address_id BIGINT REFERENCES customer_addresses(id);

ALTER TABLE orders
    ADD COLUMN delivery_recipient_name VARCHAR(160),
    ADD COLUMN delivery_phone VARCHAR(32),
    ADD COLUMN delivery_address_line1 VARCHAR(240),
    ADD COLUMN delivery_address_line2 VARCHAR(240),
    ADD COLUMN delivery_locality VARCHAR(160),
    ADD COLUMN delivery_city VARCHAR(120),
    ADD COLUMN delivery_state VARCHAR(120),
    ADD COLUMN delivery_postal_code VARCHAR(24),
    ADD COLUMN delivery_country_code VARCHAR(2),
    ADD COLUMN delivery_instructions VARCHAR(500);
