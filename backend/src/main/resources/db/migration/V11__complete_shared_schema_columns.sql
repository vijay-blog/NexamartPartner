SET SESSION group_concat_max_len = 16384;

SELECT GROUP_CONCAT(
    CONCAT('ADD COLUMN ', required.column_name, ' ', required.column_definition)
    ORDER BY required.column_name SEPARATOR ', '
)
INTO @missing_address_columns
FROM (
    SELECT 'address_line' column_name, 'VARCHAR(1000) NULL' column_definition
    UNION ALL SELECT 'city', 'VARCHAR(100) NULL'
    UNION ALL SELECT 'customer_id', 'BIGINT NULL'
    UNION ALL SELECT 'default_address', 'BOOLEAN NOT NULL DEFAULT FALSE'
    UNION ALL SELECT 'label', 'VARCHAR(100) NULL'
    UNION ALL SELECT 'latitude', 'DECIMAL(19,8) NULL'
    UNION ALL SELECT 'longitude', 'DECIMAL(19,8) NULL'
    UNION ALL SELECT 'phone', 'VARCHAR(40) NULL'
    UNION ALL SELECT 'postal_code', 'VARCHAR(30) NULL'
    UNION ALL SELECT 'recipient_name', 'VARCHAR(150) NULL'
    UNION ALL SELECT 'state', 'VARCHAR(100) NULL'
) required
LEFT JOIN information_schema.columns existing
    ON existing.table_schema = DATABASE()
    AND existing.table_name = 'addresses'
    AND existing.column_name = required.column_name
WHERE existing.column_name IS NULL;
SET @complete_addresses = IF(@missing_address_columns IS NULL, 'SELECT 1',
    CONCAT('ALTER TABLE addresses ', @missing_address_columns));
PREPARE complete_addresses_statement FROM @complete_addresses;
EXECUTE complete_addresses_statement;
DEALLOCATE PREPARE complete_addresses_statement;

SELECT GROUP_CONCAT(
    CONCAT('ADD COLUMN ', required.column_name, ' ', required.column_definition)
    ORDER BY required.column_name SEPARATOR ', '
)
INTO @missing_category_columns
FROM (
    SELECT 'active' column_name, 'BOOLEAN NOT NULL DEFAULT TRUE' column_definition
    UNION ALL SELECT 'created_at', 'TIMESTAMP(6) NULL'
    UNION ALL SELECT 'description', 'VARCHAR(1000) NULL'
    UNION ALL SELECT 'image_url', 'VARCHAR(1000) NULL'
    UNION ALL SELECT 'sort_order', 'INT NOT NULL DEFAULT 0'
    UNION ALL SELECT 'updated_at', 'TIMESTAMP(6) NULL'
) required
LEFT JOIN information_schema.columns existing
    ON existing.table_schema = DATABASE()
    AND existing.table_name = 'categories'
    AND existing.column_name = required.column_name
WHERE existing.column_name IS NULL;
SET @complete_categories = IF(@missing_category_columns IS NULL, 'SELECT 1',
    CONCAT('ALTER TABLE categories ', @missing_category_columns));
PREPARE complete_categories_statement FROM @complete_categories;
EXECUTE complete_categories_statement;
DEALLOCATE PREPARE complete_categories_statement;

SELECT GROUP_CONCAT(
    CONCAT('ADD COLUMN ', required.column_name, ' ', required.column_definition)
    ORDER BY required.column_name SEPARATOR ', '
)
INTO @missing_product_columns
FROM (
    SELECT 'available' column_name, 'BOOLEAN NOT NULL DEFAULT TRUE' column_definition
    UNION ALL SELECT 'category_id', 'BIGINT NULL'
    UNION ALL SELECT 'created_at', 'TIMESTAMP(6) NULL'
    UNION ALL SELECT 'description', 'VARCHAR(2000) NULL'
    UNION ALL SELECT 'discount', 'DECIMAL(19,2) NULL DEFAULT 0.00'
    UNION ALL SELECT 'image_url', 'VARCHAR(1000) NULL'
    UNION ALL SELECT 'price', 'DECIMAL(19,2) NOT NULL DEFAULT 0.00'
    UNION ALL SELECT 'sku', 'VARCHAR(100) NULL'
    UNION ALL SELECT 'stock', 'INT NULL DEFAULT 0'
    UNION ALL SELECT 'unit', 'VARCHAR(50) NULL'
    UNION ALL SELECT 'updated_at', 'TIMESTAMP(6) NULL'
) required
LEFT JOIN information_schema.columns existing
    ON existing.table_schema = DATABASE()
    AND existing.table_name = 'products'
    AND existing.column_name = required.column_name
WHERE existing.column_name IS NULL;
SET @complete_products = IF(@missing_product_columns IS NULL, 'SELECT 1',
    CONCAT('ALTER TABLE products ', @missing_product_columns));
PREPARE complete_products_statement FROM @complete_products;
EXECUTE complete_products_statement;
DEALLOCATE PREPARE complete_products_statement;

SELECT GROUP_CONCAT(
    CONCAT('ADD COLUMN ', required.column_name, ' ', required.column_definition)
    ORDER BY required.column_name SEPARATOR ', '
)
INTO @missing_order_item_columns
FROM (
    SELECT 'line_total' column_name, 'DECIMAL(19,2) NOT NULL DEFAULT 0.00' column_definition
    UNION ALL SELECT 'order_id', 'BIGINT NULL'
    UNION ALL SELECT 'product_id', 'BIGINT NULL'
    UNION ALL SELECT 'product_name', 'VARCHAR(255) NULL'
    UNION ALL SELECT 'quantity', 'INT NOT NULL DEFAULT 1'
    UNION ALL SELECT 'unit_price', 'DECIMAL(19,2) NOT NULL DEFAULT 0.00'
) required
LEFT JOIN information_schema.columns existing
    ON existing.table_schema = DATABASE()
    AND existing.table_name = 'order_items'
    AND existing.column_name = required.column_name
WHERE existing.column_name IS NULL;
SET @complete_order_items = IF(@missing_order_item_columns IS NULL, 'SELECT 1',
    CONCAT('ALTER TABLE order_items ', @missing_order_item_columns));
PREPARE complete_order_items_statement FROM @complete_order_items;
EXECUTE complete_order_items_statement;
DEALLOCATE PREPARE complete_order_items_statement;

SELECT GROUP_CONCAT(
    CONCAT('ADD COLUMN ', required.column_name, ' ', required.column_definition)
    ORDER BY required.column_name SEPARATOR ', '
)
INTO @missing_notification_columns
FROM (
    SELECT 'action_url' column_name, 'VARCHAR(1000) NULL' column_definition
    UNION ALL SELECT 'created_at', 'TIMESTAMP(6) NULL'
    UNION ALL SELECT 'message', 'VARCHAR(1000) NULL'
    UNION ALL SELECT 'order_id', 'BIGINT NULL'
    UNION ALL SELECT 'read_flag', 'BOOLEAN NOT NULL DEFAULT FALSE'
    UNION ALL SELECT 'title', 'VARCHAR(255) NULL'
    UNION ALL SELECT 'type', 'VARCHAR(40) NULL'
    UNION ALL SELECT 'user_id', 'BIGINT NULL'
) required
LEFT JOIN information_schema.columns existing
    ON existing.table_schema = DATABASE()
    AND existing.table_name = 'notifications'
    AND existing.column_name = required.column_name
WHERE existing.column_name IS NULL;
SET @complete_notifications = IF(@missing_notification_columns IS NULL, 'SELECT 1',
    CONCAT('ALTER TABLE notifications ', @missing_notification_columns));
PREPARE complete_notifications_statement FROM @complete_notifications;
EXECUTE complete_notifications_statement;
DEALLOCATE PREPARE complete_notifications_statement;

SELECT GROUP_CONCAT(
    CONCAT('ADD COLUMN ', required.column_name, ' ', required.column_definition)
    ORDER BY required.column_name SEPARATOR ', '
)
INTO @missing_user_columns
FROM (
    SELECT 'created_at' column_name, 'TIMESTAMP(6) NULL' column_definition
    UNION ALL SELECT 'email', 'VARCHAR(255) NULL'
    UNION ALL SELECT 'last_active_at', 'TIMESTAMP(6) NULL'
    UNION ALL SELECT 'name', 'VARCHAR(150) NULL'
    UNION ALL SELECT 'password_hash', 'VARCHAR(255) NULL'
    UNION ALL SELECT 'phone', 'VARCHAR(40) NULL'
    UNION ALL SELECT 'role', 'VARCHAR(30) NULL'
    UNION ALL SELECT 'status', 'VARCHAR(30) NULL'
    UNION ALL SELECT 'username', 'VARCHAR(100) NULL'
) required
LEFT JOIN information_schema.columns existing
    ON existing.table_schema = DATABASE()
    AND existing.table_name = 'users'
    AND existing.column_name = required.column_name
WHERE existing.column_name IS NULL;
SET @complete_users = IF(@missing_user_columns IS NULL, 'SELECT 1',
    CONCAT('ALTER TABLE users ', @missing_user_columns));
PREPARE complete_users_statement FROM @complete_users;
EXECUTE complete_users_statement;
DEALLOCATE PREPARE complete_users_statement;

SELECT GROUP_CONCAT(
    CONCAT('ADD COLUMN ', required.column_name, ' ', required.column_definition)
    ORDER BY required.column_name SEPARATOR ', '
)
INTO @missing_profile_columns
FROM (
    SELECT 'available' column_name, 'BOOLEAN NOT NULL DEFAULT FALSE' column_definition
    UNION ALL SELECT 'license_reference', 'VARCHAR(150) NULL'
    UNION ALL SELECT 'updated_at', 'TIMESTAMP(6) NULL'
    UNION ALL SELECT 'vehicle_number', 'VARCHAR(100) NULL'
    UNION ALL SELECT 'vehicle_type', 'VARCHAR(100) NULL'
    UNION ALL SELECT 'verification_status', 'VARCHAR(30) NOT NULL DEFAULT ''PENDING'''
) required
LEFT JOIN information_schema.columns existing
    ON existing.table_schema = DATABASE()
    AND existing.table_name = 'delivery_partner_profiles'
    AND existing.column_name = required.column_name
WHERE existing.column_name IS NULL;
SET @complete_profiles = IF(@missing_profile_columns IS NULL, 'SELECT 1',
    CONCAT('ALTER TABLE delivery_partner_profiles ', @missing_profile_columns));
PREPARE complete_profiles_statement FROM @complete_profiles;
EXECUTE complete_profiles_statement;
DEALLOCATE PREPARE complete_profiles_statement;

SELECT GROUP_CONCAT(
    CONCAT('ADD COLUMN ', required.column_name, ' ', required.column_definition)
    ORDER BY required.column_name SEPARATOR ', '
)
INTO @missing_earning_columns
FROM (
    SELECT 'amount' column_name, 'DECIMAL(19,2) NOT NULL DEFAULT 0.00' column_definition
    UNION ALL SELECT 'currency_code', 'VARCHAR(10) NOT NULL DEFAULT ''INR'''
    UNION ALL SELECT 'description', 'VARCHAR(500) NULL'
    UNION ALL SELECT 'earned_at', 'TIMESTAMP(6) NULL'
    UNION ALL SELECT 'order_id', 'BIGINT NULL'
    UNION ALL SELECT 'partner_id', 'BIGINT NULL'
    UNION ALL SELECT 'status', 'VARCHAR(30) NOT NULL DEFAULT ''AVAILABLE'''
) required
LEFT JOIN information_schema.columns existing
    ON existing.table_schema = DATABASE()
    AND existing.table_name = 'earnings'
    AND existing.column_name = required.column_name
WHERE existing.column_name IS NULL;
SET @complete_earnings = IF(@missing_earning_columns IS NULL, 'SELECT 1',
    CONCAT('ALTER TABLE earnings ', @missing_earning_columns));
PREPARE complete_earnings_statement FROM @complete_earnings;
EXECUTE complete_earnings_statement;
DEALLOCATE PREPARE complete_earnings_statement;
