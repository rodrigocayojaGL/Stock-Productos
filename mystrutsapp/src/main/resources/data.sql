CREATE TABLE product (
    product_id VARCHAR(50) PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    sku VARCHAR(50) NOT NULL,
    unit_of_measure VARCHAR(50) NOT NULL,
    cost DECIMAL(10,2) NOT NULL,
    sale_price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    location VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL
);

-- Ensure product_id values exist in the product table
-- Ensure product_id values exist in the product table
INSERT INTO product (product_id, product_name, sku, unit_of_measure, cost, sale_price, category, location, active)
VALUES
('P001', 'Product A', 'SKU001', 'Unit', 10.00, 15.00, 'Category A', 'Location A', TRUE),
('P002', 'Product B', 'SKU002', 'Unit', 20.00, 25.00, 'Category B', 'Location B', TRUE),
('P003', 'Product C', 'SKU003', 'Unit', 30.00, 35.00, 'Category C', 'Location C', TRUE),
('P004', 'Product D', 'SKU004', 'Unit', 40.00, 45.00, 'Category D', 'Location D', TRUE),
('P005', 'Product E', 'SKU005', 'Unit', 50.00, 55.00, 'Category E', 'Location E', TRUE);
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO users (username, password) VALUES ('admin', 'admin123');
INSERT INTO users (username, password) VALUES ('user', 'user123');

INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_ADMIN');
INSERT INTO user_roles (user_id, role) VALUES (2, 'USER');



-- Schema for inventory_movement table
CREATE TABLE inventory_movement (
    movement_id VARCHAR(255) PRIMARY KEY,
    date TIMESTAMP,
    product_id VARCHAR(255),
    movement_type VARCHAR(255),
    quantity INT,
    order_id VARCHAR(255),
    notes TEXT
);

-- Insert sample data into inventory_movement table
INSERT INTO inventory_movement (movement_id, date, product_id, movement_type, quantity, order_id, notes)
VALUES
('M001', '2023-10-01 10:00:00', 'P001', 'IN', 50, 'O001', 'Initial stock addition'),
('M002', '2023-10-02 12:00:00', 'P002', 'OUT', 20, 'O002', 'Order shipment');

-- Schema for current_stock table
CREATE TABLE current_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    total_inventory_cost DECIMAL(15, 2) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- Insert sample data into current_stock table
INSERT INTO current_stock (product_id, quantity, last_updated, total_inventory_cost)
VALUES
('P001', 100, '2023-10-01 10:00:00', 5000.00),
('P002', 50, '2023-10-02 11:00:00', 2500.00),
('P003', 200, '2023-10-03 12:00:00', 10000.00),
('P004', 75, '2023-10-04 13:00:00', 3750.00),
('P005', 150, '2023-10-05 14:00:00', 7500.00);

-- Schema for predictor_stock table
CREATE TABLE predictor_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    units_sold INT NOT NULL,
    avg_sale_price DECIMAL(15, 2) NOT NULL,
    promotion_active BOOLEAN NOT NULL,
    special_event VARCHAR(255),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- Insert sample data into predictor_stock table
INSERT INTO predictor_stock (date, product_id, units_sold, avg_sale_price, promotion_active, special_event)
VALUES
('2023-10-01 10:00:00', 'P001', 50, 25.00, TRUE, 'Holiday Sale'),
('2023-10-02 11:00:00', 'P002', 30, 15.00, FALSE, NULL),
('2023-10-03 12:00:00', 'P003', 100, 50.00, TRUE, 'Black Friday'),
('2023-10-04 13:00:00', 'P004', 20, 10.00, FALSE, NULL),
('2023-10-05 14:00:00', 'P005', 75, 35.00, TRUE, 'End of Season Sale');