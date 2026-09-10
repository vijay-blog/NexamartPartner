SET @category_sort_order_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'categories'
      AND column_name = 'sort_order'
);

SET @add_category_sort_order = IF(
    @category_sort_order_exists = 0,
    'ALTER TABLE categories ADD COLUMN sort_order INT NOT NULL DEFAULT 0',
    'SELECT 1'
);

PREPARE add_category_sort_order_statement FROM @add_category_sort_order;
EXECUTE add_category_sort_order_statement;
DEALLOCATE PREPARE add_category_sort_order_statement;

SET @category_image_url_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'categories'
      AND column_name = 'image_url'
);

SET @add_category_image_url = IF(
    @category_image_url_exists = 0,
    'ALTER TABLE categories ADD COLUMN image_url VARCHAR(1000) NULL',
    'SELECT 1'
);

PREPARE add_category_image_url_statement FROM @add_category_image_url;
EXECUTE add_category_image_url_statement;
DEALLOCATE PREPARE add_category_image_url_statement;
