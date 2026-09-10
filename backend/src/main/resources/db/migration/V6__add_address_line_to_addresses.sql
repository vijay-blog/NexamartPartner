SET @address_line_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'addresses'
      AND column_name = 'address_line'
);

SET @add_address_line = IF(
    @address_line_exists = 0,
    'ALTER TABLE addresses ADD COLUMN address_line VARCHAR(1000) NULL',
    'SELECT 1'
);

PREPARE add_address_line_statement FROM @add_address_line;
EXECUTE add_address_line_statement;
DEALLOCATE PREPARE add_address_line_statement;
