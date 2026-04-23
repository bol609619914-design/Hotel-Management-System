USE hotel_management;

CREATE TABLE IF NOT EXISTS financial_transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    reservation_no VARCHAR(40) NOT NULL,
    transaction_type VARCHAR(64) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    direction VARCHAR(32) NOT NULL,
    remark VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_financial_transaction_reservation FOREIGN KEY (reservation_id) REFERENCES reservation(id),
    INDEX idx_financial_transaction_reservation (reservation_id, created_at),
    INDEX idx_financial_transaction_type (transaction_type, direction)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NULL,
    room_id BIGINT NULL,
    operator_username VARCHAR(64) NOT NULL,
    operator_role VARCHAR(32) NOT NULL,
    action_type VARCHAR(64) NOT NULL,
    description VARCHAR(255) NOT NULL,
    before_snapshot VARCHAR(500),
    after_snapshot VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_operation_log_reservation FOREIGN KEY (reservation_id) REFERENCES reservation(id),
    CONSTRAINT fk_operation_log_room FOREIGN KEY (room_id) REFERENCES room(id),
    INDEX idx_operation_log_reservation (reservation_id, created_at),
    INDEX idx_operation_log_operator (operator_username, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS notification_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category VARCHAR(64) NOT NULL,
    title VARCHAR(128) NOT NULL,
    content VARCHAR(255) NOT NULL,
    related_type VARCHAR(32) NOT NULL,
    related_id BIGINT NOT NULL,
    target_role VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'UNREAD',
    scheduled_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at DATETIME NULL,
    INDEX idx_notification_target (target_role, status, scheduled_at),
    INDEX idx_notification_related (related_type, related_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
