-- init_db.sql

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    uuid VARCHAR(50) PRIMARY KEY,                  -- Unique identifier for each product (generated in Java)
    variant_id VARCHAR(50),                 -- Original variant ID (can be NULL or duplicated)
    product_id INTEGER NOT NULL,
    size_label VARCHAR(20),
    product_name VARCHAR(100),
    brand VARCHAR(50),
    color VARCHAR(30),
    age_group VARCHAR(20),
    gender VARCHAR(10),
    size_type VARCHAR(20),
    product_type VARCHAR(100),
    is_duplicate BOOLEAN NOT NULL DEFAULT FALSE -- Flag to indicate duplicate variant IDs
);

-- Indexes for frequently queried fields
CREATE INDEX idx_variant_id ON products(variant_id); -- Index for variant_id to improve searches
CREATE INDEX idx_product_id ON products(product_id);
CREATE INDEX idx_brand ON products(brand);