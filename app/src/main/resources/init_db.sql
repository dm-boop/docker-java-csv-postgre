-- init_db.sql

-- Create products table -- We will revisit the sizes!
CREATE TABLE IF NOT EXISTS products (
    variant_id VARCHAR(50) PRIMARY KEY,
    product_id INTEGER NOT NULL,
    size_label VARCHAR(20),
    product_name VARCHAR(100),
    brand VARCHAR(50),
    color VARCHAR(30),
    age_group VARCHAR(20),
    gender VARCHAR(10),
    size_type VARCHAR(20),
    product_type VARCHAR(100)
);

-- Optionally we add indexes for frequently queried things
CREATE INDEX idx_product_id ON products(product_id);
CREATE INDEX idx_brand ON products(brand);