# Java Brand Canonicalization and Product Data Pipeline

This is a hobby project designed to explore Java features while building a product data pipeline that integrates with PostgreSQL. The project includes more complex logic for handling canonicalization of brand names, ensuring a unified representation of data even when the input is inconsistent or redundant.

---

## Features

- **PostgreSQL Integration**: 
  - Automatically sets up a `productdb` database using Docker and initializes it with a `products` table.
  - Supports importing product data from a CSV file and storing it in the database.
  
- **Canonicalization of Brand Names**: 
  - Implements sophisticated logic to normalize and group brand names based on shared prefixes and rules, ensuring that similar variations of the same brand (e.g., "Adidas Kids", "ADIDAS", "Adidas Sportswear") are reduced to a single canonical name.
  - Sub-brands are identified and separated where appropriate (e.g., "Adidas Kids" becomes canonical `Adidas` with sub-brand `Kids`).

- **Error Handling**:
  - Gracefully handles invalid or missing data (e.g., `null` values in CSV fields).
  - Ensures that all brand normalization and canonicalization processes are robust.

---

## Prerequisites

1. **Docker**: Ensure Docker is installed and running.
2. **Java Development Environment**:
   - Java 17+ (for modern features and performance).
   - Maven or Gradle for dependency management.
3. **CSV File**:
   - Place a CSV file with the required structure in `./app/src/main/resources/data.csv`.
   - An example CSVs are provided in the repository and must be renamed to `data.csv` before usage.

---

## How to Run

### Step 1: Start PostgreSQL and the Java application runs on its own
Run the following command to start the PostgreSQL container and initialize the database:
```bash
docker-compose up -d
```

**Note:** The `init_db.sql` script will set up the `products` table only if no existing volume is detected. To reset the database:
```bash
docker-compose down -v
```


### Step 2: Verify PostgreSQL is Running
To connect to the database and verify the setup, use:
```bash
docker exec -it product_data_pipeline_db psql -U productuser -d productdb
```

Within the PostgreSQL CLI:
Check table structure:
```sql
\dt;
```
Validate data insertion with:
```sql
SELECT * FROM products;
```

---

## File Structure
### Key Files and Directories
- `docker-compose.yml`: Configures the PostgreSQL container.
- `init_db.sql`: SQL script to initialize the products table.
- `app/src/main/java/org/example/`: Contains the core Java logic, including:
  - `Product class`: Handles the product entries.
  - `Brand class`: Handles brand normalization and canonicalization logic.
  - `Main class`: Entry point of the application.
- `app/src/main/resources/data.csv`: CSV file with product data to be imported.


### Canonicalization Logic
The application includes advanced logic for brand normalization:
- Brands are normalized to a consistent format (e.g., capitalization and spacing rules).
- Shortest names are selected as canonical names when multiple variations of a brand exist.
- Sub-brands are identified and separated (e.g., "Adidas Kids" becomes canonical `Adidas` with sub-brand `Kids`).
- Handles edge cases like null or empty strings, as well as variations with additional or missing words.

Example Input CSV:
```csv
variant_id,product_id,size_label,product_name,brand,color,age_group,gender,size_type,product_type
16581143-22,16581143,27 Waist,Pinch Waist Denim Shorts,Adidas,Blue,Adult,Female,regular,Clothing > Denim > Shorts
16581143-23,16581143,28 Waist,Riley High Waist Cropped Jeans,ADIDAS,Dark Blue,Adult,Unisex,regular,Clothing > Denim
```
Canonicalized Output:
```csv
canonical_brand,sub_brand,original_brand
Adidas,,Adidas
Adidas,,ADIDAS
```

---

## Potential Improvements
### 1. Unit Testing
- Add comprehensive unit tests for:
  - Brand normalization and canonicalization logic.
  - CSV parsing and validation.
  - Database interactions.
### 2. Automated Testing
- Use frameworks like JUnit and Mockito to automate testing of edge cases and database transactions.
### 3. Logic Optimization
- Store the original brand names in the database as well
  - Potentially Create a separate Brands table and only add reference to brands when inserting new products
- Introduce more sophisticated logics for merging product duplicates, handling different languages for the same fields, etc.
### 4. Performance Optimization
- Refactor brand canonicalization logic for efficiency, especially for large datasets.
  - Use `HashMap` or `Trie` structures for faster lookups during normalization.
- Optimize database queries by adding indexes for frequently filtered or joined columns.
### 5. Logging and Monitoring
- Integrate a logging framework (e.g., SLF4J with Logback) for better debugging and traceability.
- Add monitoring tools like Prometheus or Grafana for database and application metrics.
### 6. UI for Data Management
- Build a simple front-end or CLI interface for managing products and brands.
### 7. Configuration Improvements
- Use `.env` files or Java configuration libraries (e.g., Spring Boot `application.properties`) for easier management of environment variables.
### 8. Docker Enhancements
- Add a health check to the PostgreSQL container in `docker-compose.yml`.