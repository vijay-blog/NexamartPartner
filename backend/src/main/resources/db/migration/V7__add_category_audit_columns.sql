SET @category_created_at_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'categories'
      AND column_name = 'created_at'
);

SET @add_category_created_at = IF(
    @category_created_at_exists = 0,
    'ALTER TABLE categories ADD COLUMN created_at TIMESTAMP(6) NULL',
    'SELECT 1'
);

PREPARE add_category_created_at_statement FROM @add_category_created_at;
EXECUTE add_category_created_at_statement;
DEALLOCATE PREPARE add_category_created_at_statement;

SET @category_updated_at_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'categories'
      AND column_name = 'updated_at'
);

SET @add_category_updated_at = IF(
    @category_updated_at_exists = 0,
    'ALTER TABLE categories ADD COLUMN updated_at TIMESTAMP(6) NULL',
    'SELECT 1'
);

PREPARE add_category_updated_at_statement FROM @add_category_updated_at;
EXECUTE add_category_updated_at_statement;
DEALLOCATE PREPARE add_category_updated_at_statement;
