-- init_db.sql

-- Create products table
CREATE TABLE products (
    uuid VARCHAR(50) PRIMARY KEY,
    variant_id VARCHAR(50),
    product_id INTEGER NOT NULL,
    size_label VARCHAR(50),
    product_name VARCHAR(200),
    brand_id INTEGER REFERENCES brands(brand_id),
    color VARCHAR(50),
    age_group VARCHAR(50),
    gender VARCHAR(50),
    size_type VARCHAR(50),
    product_type VARCHAR(200),
    is_duplicate BOOLEAN NOT NULL DEFAULT FALSE -- Flag to indicate duplicate variant IDs
);

-- Create brands table
CREATE TABLE brands (
    brand_id SERIAL PRIMARY KEY,
    brand_name VARCHAR(100) UNIQUE
);

-- Indexes for frequently queried fields
CREATE INDEX idx_variant_id ON products(variant_id);
CREATE INDEX idx_product_id ON products(product_id);
CREATE INDEX idx_brand_id ON products(brand_id);
--brand_name Automatically created by UNIQUE constraint
