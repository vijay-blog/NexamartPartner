CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    username VARCHAR(100) UNIQUE,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(40),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    last_active_at TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS delivery_partner_profiles (
    user_id BIGINT PRIMARY KEY,
    verification_status VARCHAR(30) NOT NULL,
    vehicle_type VARCHAR(100),
    vehicle_number VARCHAR(100),
    license_reference VARCHAR(150),
    available BOOLEAN NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_partner_profile_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255),
    message VARCHAR(1000),
    created_at TIMESTAMP(6) NOT NULL,
    read_flag BOOLEAN NOT NULL,
    type VARCHAR(40),
    order_id BIGINT,
    action_url VARCHAR(1000),
    INDEX idx_notifications_user (user_id, read_flag),
    CONSTRAINT fk_partner_notification_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    quantity INT NOT NULL,
    line_total DECIMAL(19,2) NOT NULL,
    CONSTRAINT fk_partner_item_order
        FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_partner_item_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS earnings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    partner_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    earned_at TIMESTAMP(6) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    status VARCHAR(30) NOT NULL,
    description VARCHAR(500),
    INDEX idx_earnings_partner_date (partner_id, earned_at),
    CONSTRAINT fk_partner_earning_user
        FOREIGN KEY (partner_id) REFERENCES users(id),
    CONSTRAINT fk_partner_earning_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
);
