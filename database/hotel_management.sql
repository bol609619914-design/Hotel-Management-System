CREATE DATABASE IF NOT EXISTS hotel_management
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_0900_ai_ci;

USE hotel_management;

DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS customer_user;
DROP TABLE IF EXISTS admin_user;
DROP TABLE IF EXISTS guest;
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS room_type;

CREATE TABLE room_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    max_guests INT NOT NULL,
    bed_type VARCHAR(64) NOT NULL,
    area INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    amenities VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(32) NOT NULL UNIQUE,
    room_type_id BIGINT NOT NULL,
    floor INT NOT NULL,
    status VARCHAR(32) NOT NULL,
    clean_status VARCHAR(32) NOT NULL,
    CONSTRAINT fk_room_room_type FOREIGN KEY (room_type_id) REFERENCES room_type(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE guest (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(64) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    id_card VARCHAR(32) NOT NULL,
    member_level VARCHAR(32) NOT NULL,
    remark VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE admin_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(64) NOT NULL,
    role VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE customer_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(64) NOT NULL,
    member_level VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_no VARCHAR(40) NOT NULL UNIQUE,
    guest_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    guest_count INT NOT NULL,
    room_fee DECIMAL(10, 2) NOT NULL DEFAULT 0,
    breakfast_fee DECIMAL(10, 2) NOT NULL DEFAULT 0,
    extra_bed_fee DECIMAL(10, 2) NOT NULL DEFAULT 0,
    deposit_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    coupon_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    channel VARCHAR(32) NOT NULL,
    special_request VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actual_check_in_time DATETIME NULL,
    actual_check_out_time DATETIME NULL,
    CONSTRAINT fk_reservation_guest FOREIGN KEY (guest_id) REFERENCES guest(id),
    CONSTRAINT fk_reservation_room FOREIGN KEY (room_id) REFERENCES room(id),
    INDEX idx_reservation_room_dates (room_id, check_in_date, check_out_date),
    INDEX idx_reservation_status_dates (status, check_in_date, check_out_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO room_type (name, base_price, max_guests, bed_type, area, description, amenities) VALUES
('Urban Queen', 498.00, 2, '1.8m Queen Bed', 32, 'Suitable for business trips and city short stays.', 'Breakfast, WiFi, Smart TV'),
('Garden Twin', 568.00, 2, '2 x 1.2m Twin Bed', 36, 'Quiet floor with courtyard-facing windows.', 'Breakfast, WiFi, Tea Set'),
('Executive Suite', 968.00, 4, '1.8m Queen Bed + Sofa', 62, 'Living room layout for family or VIP guests.', 'Breakfast, Mini Bar, Bathtub');

INSERT INTO room (room_number, room_type_id, floor, status, clean_status) VALUES
('801', 1, 8, 'AVAILABLE', 'READY'),
('802', 1, 8, 'AVAILABLE', 'READY'),
('901', 2, 9, 'AVAILABLE', 'READY'),
('902', 2, 9, 'OCCUPIED', 'CLEANING'),
('1001', 3, 10, 'AVAILABLE', 'READY'),
('1002', 3, 10, 'MAINTENANCE', 'BLOCKED');

INSERT INTO guest (full_name, phone, id_card, member_level, remark) VALUES
('林若川', '13800000001', '330102199110101234', 'GOLD', 'Late check-in'),
('周清禾', '13800000002', '330102199305052456', 'REGULAR', 'Corporate booking'),
('沈嘉屿', '13800000003', '330102199512128888', 'PLATINUM', 'Needs airport pickup');

INSERT INTO customer_user (username, phone, password, display_name, member_level, status) VALUES
('13900000001', '13900000001', '$2a$10$VvN31onlQ0j5W1D2Laj0zuzQO2S4M0nB6fTP3D6JrIoYNewc0hXtS', '住客示例', 'REGULAR', 'ACTIVE');

INSERT INTO reservation (
    reservation_no, guest_id, room_id, check_in_date, check_out_date, guest_count,
    room_fee, breakfast_fee, extra_bed_fee, deposit_amount, coupon_amount, total_amount,
    status, channel, special_request, created_at
) VALUES
('RES20260422080001', 1, 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 2, 996.00, 68.00, 0.00, 300.00, 50.00, 1314.00, 'BOOKED', 'DIRECT', 'Window side preferred', NOW()),
('RES20260421093015', 2, 4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 2, 1136.00, 88.00, 0.00, 300.00, 0.00, 1524.00, 'CHECKED_IN', 'OTA', 'Need extra towel', NOW()),
('RES20260420114530', 3, 5, DATE_ADD(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 3, 1936.00, 128.00, 160.00, 500.00, 100.00, 2624.00, 'BOOKED', 'DIRECT', 'Family crib', NOW());
