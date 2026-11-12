CREATE TABLE businesses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    business_type VARCHAR(80) NOT NULL,
    phone VARCHAR(32),
    email VARCHAR(254),
    address TEXT,
    currency CHAR(3) NOT NULL DEFAULT 'INR',
    timezone VARCHAR(80) NOT NULL DEFAULT 'Asia/Kolkata',
    status VARCHAR(24) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_business_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL REFERENCES businesses(id),
    name VARCHAR(160) NOT NULL,
    email VARCHAR(254) NOT NULL,
    phone VARCHAR(32),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_user_role CHECK (role IN ('PLATFORM_ADMIN', 'BUSINESS_OWNER', 'BUSINESS_MANAGER', 'EMPLOYEE')),
    CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_users_business_id ON users(business_id);
CREATE INDEX idx_users_business_role ON users(business_id, role);
