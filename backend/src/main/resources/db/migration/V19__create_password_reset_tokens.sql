CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    customer_id BIGINT REFERENCES customers(id) ON DELETE CASCADE,
    token_hash CHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_password_reset_token_owner CHECK (
        (user_id IS NOT NULL AND customer_id IS NULL) OR
        (user_id IS NULL AND customer_id IS NOT NULL)
    )
);

CREATE INDEX ix_password_reset_tokens_user ON password_reset_tokens(user_id);
CREATE INDEX ix_password_reset_tokens_customer ON password_reset_tokens(customer_id);
CREATE INDEX ix_password_reset_tokens_expiry ON password_reset_tokens(expires_at);
