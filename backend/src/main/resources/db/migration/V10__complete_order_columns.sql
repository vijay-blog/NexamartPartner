SET SESSION group_concat_max_len = 8192;

SELECT GROUP_CONCAT(
    CONCAT('ADD COLUMN ', required_columns.column_name, ' ', required_columns.column_definition)
    ORDER BY required_columns.column_name
    SEPARATOR ', '
)
INTO @missing_order_columns
FROM (
    SELECT 'accepted_at' AS column_name, 'TIMESTAMP(6) NULL' AS column_definition
    UNION ALL SELECT 'cancellation_reason', 'VARCHAR(1000) NULL'
    UNION ALL SELECT 'created_at', 'TIMESTAMP(6) NULL'
    UNION ALL SELECT 'currency_code', 'VARCHAR(10) NOT NULL DEFAULT ''INR'''
    UNION ALL SELECT 'customer_id', 'BIGINT NULL'
    UNION ALL SELECT 'delivered_at', 'TIMESTAMP(6) NULL'
    UNION ALL SELECT 'delivery_address_id', 'BIGINT NULL'
    UNION ALL SELECT 'delivery_fee', 'DECIMAL(19,2) NOT NULL DEFAULT 0.00'
    UNION ALL SELECT 'delivery_partner_id', 'BIGINT NULL'
    UNION ALL SELECT 'out_for_delivery_at', 'TIMESTAMP(6) NULL'
    UNION ALL SELECT 'payment_method', 'VARCHAR(30) NULL'
    UNION ALL SELECT 'payment_status', 'VARCHAR(30) NULL'
    UNION ALL SELECT 'picked_up_at', 'TIMESTAMP(6) NULL'
    UNION ALL SELECT 'status', 'VARCHAR(40) NOT NULL DEFAULT ''PENDING'''
    UNION ALL SELECT 'subtotal', 'DECIMAL(19,2) NOT NULL DEFAULT 0.00'
    UNION ALL SELECT 'total', 'DECIMAL(19,2) NOT NULL DEFAULT 0.00'
    UNION ALL SELECT 'updated_at', 'TIMESTAMP(6) NULL'
    UNION ALL SELECT 'version', 'BIGINT NOT NULL DEFAULT 0'
) required_columns
LEFT JOIN information_schema.columns existing_columns
    ON existing_columns.table_schema = DATABASE()
    AND existing_columns.table_name = 'orders'
    AND existing_columns.column_name = required_columns.column_name
WHERE existing_columns.column_name IS NULL;

SET @complete_orders = IF(
    @missing_order_columns IS NULL,
    'SELECT 1',
    CONCAT('ALTER TABLE orders ', @missing_order_columns)
);

PREPARE complete_orders_statement FROM @complete_orders;
EXECUTE complete_orders_statement;
DEALLOCATE PREPARE complete_orders_statement;
