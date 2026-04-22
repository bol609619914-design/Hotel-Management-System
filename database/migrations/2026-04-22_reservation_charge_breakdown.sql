USE hotel_management;

SET @ddl = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'reservation'
          AND column_name = 'room_fee'
    ),
    'SELECT 1',
    'ALTER TABLE reservation ADD COLUMN room_fee DECIMAL(10, 2) NOT NULL DEFAULT 0 AFTER guest_count'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'reservation'
          AND column_name = 'breakfast_fee'
    ),
    'SELECT 1',
    'ALTER TABLE reservation ADD COLUMN breakfast_fee DECIMAL(10, 2) NOT NULL DEFAULT 0 AFTER room_fee'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'reservation'
          AND column_name = 'extra_bed_fee'
    ),
    'SELECT 1',
    'ALTER TABLE reservation ADD COLUMN extra_bed_fee DECIMAL(10, 2) NOT NULL DEFAULT 0 AFTER breakfast_fee'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'reservation'
          AND column_name = 'deposit_amount'
    ),
    'SELECT 1',
    'ALTER TABLE reservation ADD COLUMN deposit_amount DECIMAL(10, 2) NOT NULL DEFAULT 0 AFTER extra_bed_fee'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'reservation'
          AND column_name = 'coupon_amount'
    ),
    'SELECT 1',
    'ALTER TABLE reservation ADD COLUMN coupon_amount DECIMAL(10, 2) NOT NULL DEFAULT 0 AFTER deposit_amount'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE reservation
SET room_fee = total_amount
WHERE room_fee = 0
  AND total_amount > 0;
